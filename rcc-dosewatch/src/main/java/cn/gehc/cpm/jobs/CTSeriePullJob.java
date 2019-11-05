package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.CTStudy;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.CTSerieRepository;
import cn.gehc.cpm.repository.CTStudyRepository;
import cn.gehc.cpm.repository.StudyRepository;
import cn.gehc.cpm.util.DataUtil;
import cn.gehc.cpm.util.SerieType;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "ctSeriePullJob")
public class CTSeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(CTSeriePullJob.class);

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private CTStudyRepository ctStudyRepository;

    @Autowired
    private CTSerieRepository ctSerieRepository;

    public void insertData(@Headers Map<String, Object> headers, @Body Object body) {
        log.info("start to insert/update data to ct_serie");
        List<Map<String, Object>> dataMap = (List<Map<String, Object>>) body;

        Set<Study> studiesFromJob = new HashSet<>();
        Set<CTStudy> ctStudySet = new HashSet<>();
        Set<CTSerie> ctSerieSet = new HashSet<>();

        Map<String, TreeSet<CTSerie>> studyWithSeriesMap = new HashMap<>();

        Long lastPolledValue = null;
        Study study;
        CTStudy ctStudy;
        CTSerie ctSerie;
        // save study, ct_study, ct_serie
        for(Map<String, Object> serieProps : dataMap) {
            log.debug(serieProps.toString());
            study = DataUtil.convertProps2Study(serieProps);
            studiesFromJob.add(study);

            ctStudy = DataUtil.convertProps2CTStudy(serieProps);
            ctStudySet.add(ctStudy);

            ctSerie = DataUtil.convertProps2CTSerie(serieProps);
            ctSerieSet.add(ctSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            if(lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }
        }

        if(ctStudySet.size() > 0) {
            ctStudyRepository.saveAll(ctStudySet);
        }

        if(ctSerieSet.size() > 0) {
            ctSerieRepository.saveAll(ctSerieSet);
        }

        List<String> studyIds = studiesFromJob.stream().map(s -> s.getLocalStudyId()).collect(Collectors.toList());
        //combine studies from job(hasn't been persisted) with studies from database
        List<Study> studyList = studyRepository.findByLocalStudyIdIn(studyIds);
        for(Study st : studiesFromJob) {
            if(!studyList.contains(st)) {
                studyList.add(st);
            }
        }
        //retrieve ct serie from database
        List<CTSerie> ctSeriesFromDB = ctSerieRepository.findByLocalStudyKeyIn(studyIds);
        for(CTSerie ctse : ctSeriesFromDB) {
            TreeSet<CTSerie> ctSeries = studyWithSeriesMap.get(ctse.getLocalStudyKey());
            if(ctSeries == null) {
                ctSeries = new TreeSet<>();
            }
            ctSeries.add(ctse);
            studyWithSeriesMap.put(ctse.getLocalStudyKey(), ctSeries);
        }

        List<Study> study2Update = new ArrayList<>(studyList.size());
        for(Study tmpStudy : studyList) {
            TreeSet<CTSerie> serieSet = studyWithSeriesMap.get(tmpStudy.getLocalStudyId());
            if(serieSet != null && serieSet.size() > 0) {
                CTSerie lastCTSerie = serieSet.last();
                CTSerie firstCTSerie = serieSet.first();
                if(lastCTSerie == null || lastCTSerie.getSeriesDate() == null || lastCTSerie.getExposureTime() == null) {
                    return;
                }
                if(firstCTSerie == null || firstCTSerie.getSeriesDate() == null || firstCTSerie.getExposureTime() == null) {
                    return;
                }

                //update study start time
                tmpStudy.setStudyStartTime(firstCTSerie.getSeriesDate());
                //update study end time
                tmpStudy.setStudyEndTime(DataUtil.getLastSerieDate(serieSet.last()));

                //to calculate target region count by serie
                Long targetRegionCount = serieSet.stream()
                        .filter(serie -> StringUtils.isNotBlank(serie.getTargetRegion()))
                        .filter(serie -> !SerieType.CONSTANT_ANGLE.getType().equals(serie.getDType()))
                        .map(serie -> serie.getTargetRegion())
                        .distinct()
                        .count();
                if(targetRegionCount > 1) {
                    log.debug("***************************************************************");
                    log.debug("study: {}, target_region: {}, target region count: {}",
                            tmpStudy.getLocalStudyId(),
                            serieSet.stream().map(se -> se.getTargetRegion()).distinct().reduce((x, y) -> x + "," + y).get(),
                            targetRegionCount);
                    log.debug("***************************************************************");
                }
                tmpStudy.setTargetRegionCount(targetRegionCount.intValue());
                log.debug("study: {}, target region count: {}", tmpStudy.getLocalStudyId(), tmpStudy.getTargetRegionCount());

                study2Update.add(tmpStudy);
            }
        }

        if(study2Update.size() > 0) {
            studyRepository.saveAll(study2Update);
        }

        // TODO only apply for RJYY, will be removed for other customer
        // update prev_local_study_id and next_local_study_id
        Map<String, Set<String>> aetStudyDateMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Study s : study2Update) {
            String aet = s.getStudyKey().getAet();
            Set studyDateSet = aetStudyDateMap.get(aet);
            if(studyDateSet == null) {
                studyDateSet = new HashSet();
            }
            studyDateSet.add(sdf.format(s.getStudyDate()));
            aetStudyDateMap.put(aet, studyDateSet);
        }
        for(Map.Entry<String, Set<String>> aetStudyDate : aetStudyDateMap.entrySet()) {
            String aet = aetStudyDate.getKey();
            Set<String> studyDateSet = aetStudyDate.getValue();
            for(String studyDateString : studyDateSet) {
                List<Study> studies = studyRepository.findByAETAndStudyDateChar(aet, studyDateString);
                Study prevStudy = (studies != null && studies.size() > 0) ? studies.get(0) : null;
                Study currentStudy = null;
                for(int idx = 1; idx < studies.size(); idx++) {
                    currentStudy = studies.get(idx);
                    prevStudy.setNextLocalStudyId(currentStudy.getLocalStudyId());
                    currentStudy.setPrevLocalStudyId(prevStudy.getLocalStudyId());
                    prevStudy = currentStudy;
                }
                studyRepository.saveAll(studies);
            }
        }
        //TODO END

        if(lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }
}
