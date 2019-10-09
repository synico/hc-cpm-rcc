package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.MRSerie;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.MRSerieRepository;
import cn.gehc.cpm.repository.StudyRepository;
import cn.gehc.cpm.util.DataUtil;
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

@Service(value = "mrSeriePullJob")
public class MRSeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(MRSeriePullJob.class);

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MRSerieRepository mrSerieRepository;

    public void insertData(@Headers Map<String, Object> headers, @Body Object body) {
        log.info("start to insert data to mr_serie");
        List<Map<String, Object>> dataMap = (List<Map<String, Object>>) body;

        Set<Study> studiesFromJob = new HashSet<>();
        Set<MRSerie> mrSerieSet = new HashSet<>();

        Map<String, TreeSet<MRSerie>> studyWithSeriesMap = new HashMap<>();

        Long lastPolledValue = null;
        Study study;
        MRSerie mrSerie;
        // save study, mr_serie
        for(Map<String, Object> serieProps : dataMap) {
            log.debug(serieProps.toString());
            study = DataUtil.convertProps2Study(serieProps);
            studiesFromJob.add(study);

            mrSerie = DataUtil.convertProps2MRSerie(serieProps);
            mrSerieSet.add(mrSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            if(lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }
        }

        if(mrSerieSet.size() > 0) {
            mrSerieRepository.saveAll(mrSerieSet);
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
        List<MRSerie> mrSeriesFromDB = mrSerieRepository.findByLocalStudyKeyIn(studyIds);
        for(MRSerie mrse : mrSeriesFromDB) {
            TreeSet<MRSerie> mrSeries = studyWithSeriesMap.get(mrse.getLocalStudyKey());
            if(mrSeries == null) {
                mrSeries = new TreeSet<>();
            }
            mrSeries.add(mrse);
            studyWithSeriesMap.put(mrse.getLocalStudyKey(), mrSeries);
        }

        List<Study> study2Update = new ArrayList<>(studyList.size());
        for(Study tmpStudy : studyList) {
            TreeSet<MRSerie> serieSet = studyWithSeriesMap.get(tmpStudy.getLocalStudyId());
            if(serieSet != null && serieSet.size() > 0) {
                MRSerie lastMRSerie = serieSet.last();
                MRSerie firstMRSerie = serieSet.first();
                if(lastMRSerie == null || lastMRSerie.getSeriesDate() == null || lastMRSerie.getAcquisitionDuration() == null) {
                    return;
                }
                if(firstMRSerie == null || firstMRSerie.getSeriesDate() == null || firstMRSerie.getAcquisitionDuration() == null) {
                    return;
                }

                //update study start time
                tmpStudy.setStudyStartTime(firstMRSerie.getSeriesDate());
                //update study end time
                tmpStudy.setStudyEndTime(DataUtil.getLastSerieDate(serieSet.last()));
                study2Update.add(tmpStudy);

                //to calculate protocol by serie
                Long targetRegionCount = serieSet.stream().map(mrse -> mrse.getProtocolName())
                        .filter(protocolName -> StringUtils.isNotBlank(protocolName))
                        .distinct()
                        .count();
                if(targetRegionCount > 1) {
                    log.info("***************************************************************");
                    log.info("study: {}, target_region: {}, target region count: {}",
                            tmpStudy.getLocalStudyId(),
                            serieSet.stream().map(mrse -> mrse.getProtocolName()).distinct().reduce((x, y) -> x + "," + y).get(),
                            targetRegionCount);
                    log.info("***************************************************************");
                }
                tmpStudy.setTargetRegionCount(targetRegionCount.intValue());

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

        if(lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }

}