package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.CTStudy;
import cn.gehc.cpm.domain.OrgEntity;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.process.ct.StudyDurationProcess;
import cn.gehc.cpm.process.ct.TargetRegionCountProcess;
import cn.gehc.cpm.repository.CTSerieRepository;
import cn.gehc.cpm.repository.CTStudyRepository;
import cn.gehc.cpm.util.DataUtil;
import cn.gehc.cpm.util.SerieType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 212706300
 */
@Service(value = "ctSeriePullJob")
public class CTSeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(CTSeriePullJob.class);

    @Autowired
    private CTStudyRepository ctStudyRepository;

    @Autowired
    private CTSerieRepository ctSerieRepository;

    @Autowired
    private StudyDurationProcess studyDurationProcess;

    @Autowired
    private TargetRegionCountProcess targetRegionCountProcess;

    @Override
    public void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to insert/update data to ct_serie, [ {} ] records will be processed", body.size());

        Set<Study> studiesFromJob = new HashSet<>();
        Set<CTStudy> ctStudySet = new HashSet<>();
        Set<CTSerie> ctSerieSet = new HashSet<>();

        Map<String, TreeSet<CTSerie>> studyWithSeriesMap = new HashMap<>();

        Long lastPolledValue = null;
        Study study;
        CTStudy ctStudy;
        CTSerie ctSerie;
        Long orgId = 0L;
        // save study, ct_study, ct_serie
        for(Map<String, Object> serieProps : body) {
            log.debug(serieProps.toString());

            // retrieve org entity id by facility code
            String facilityCode = DataUtil.getStringFromProperties(serieProps, "facility_code");
            if(orgId.longValue() == 0 && StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                if(orgEntityList.size() > 0) {
                    orgId = orgEntityList.get(0).getOrgId();
                    log.info("facility < {} > has been retrieved", orgId);
                } else {
                    // !!! IMPORTANT !!! job will not save data to database since org_entity has not been properly configured
                    log.warn("The org/device has not been synchronized, job will not save data");
                    return;
                }
            }
            if(orgId.longValue() == 0 && StringUtils.isBlank(facilityCode)) {
                log.error("facility hasn't been configured for aet: < {} >", DataUtil.getStringFromProperties(serieProps, "aet"));
                continue;
            }
            serieProps.put("org_id", orgId);

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

        // since v1.1
        Set<Study> mergedStudies = this.mergeStudies(studiesFromJob);
        studyWithSeriesMap = this.buildSeriesMap(mergedStudies);
        studyDurationProcess.process(mergedStudies, studyWithSeriesMap);
        targetRegionCountProcess.process(mergedStudies, studyWithSeriesMap);

        if(mergedStudies.size() > 0) {
            studyRepository.saveAll(mergedStudies);
        }

        log.info("[ {} ] studies have been saved, [ {} ] ct studies have been saved, [ {} ] ct series have been saved",
                mergedStudies.size(), ctStudySet.size(), ctSerieSet.size());

        linkStudies(mergedStudies);

        if(lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }

    /**
     * For CT series, we need to group series by type(dtype), then check the scan range
     * to define if there are repeated series in the study
     * @param ctSeries
     * @return Boolean
     */
    private Boolean hasRepeatedSeries(Set<CTSerie> ctSeries) {
        Boolean hasRepeatedSeries = Boolean.FALSE;
        Map<String, List<CTSerie>> seriesByType = new HashMap<>();
        ctSeries.stream().filter(serie -> !SerieType.CONSTANT_ANGLE.getType().equals(serie.getDType()))
            .forEach(serie -> {
                List<CTSerie> serieList = seriesByType.get(serie.getDType());
                if(serieList == null) {
                    serieList = new ArrayList<>();
                }
                serieList.add(serie);
                seriesByType.put(serie.getDType(), serieList);
            });

        for(Map.Entry<String, List<CTSerie>> seriesEntry : seriesByType.entrySet()) {
            List<CTSerie> ctSerieList = seriesEntry.getValue();
            List<CTSerie> series2Compare;
            Set<CTSerie> series2Update = new HashSet<>();
            if(ctSerieList.size() > 1) {
                for(CTSerie baseSerie : ctSerieList) {
                    series2Compare = ctSerieList.stream()
                        .filter(serie -> baseSerie.getLocalSerieId() != serie.getLocalSerieId())
                        .collect(Collectors.toList());
                    for(CTSerie ctSerie : series2Compare) {
                        //start slice location
                        if(ctSerie.getStartSliceLocation() > baseSerie.getStartSliceLocation()
                            && ctSerie.getStartSliceLocation() < baseSerie.getEndSliceLocation()) {
                            log.info("study {} has repeated series", ctSerie.getLocalStudyKey());
                            if(log.isDebugEnabled()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    log.debug("base serie: {}", objectMapper.writeValueAsString(baseSerie));
                                    log.debug("current serie: {}", objectMapper.writeValueAsString(ctSerie));
                                } catch(Exception ex) {
                                }
                            }
                            baseSerie.setIsRepeated(Boolean.TRUE);
                            ctSerie.setIsRepeated(Boolean.TRUE);
                            series2Update.add(baseSerie);
                            series2Update.add(ctSerie);
                            hasRepeatedSeries = Boolean.TRUE;
                        }
                        //end slice location
                        if(ctSerie.getEndSliceLocation() > baseSerie.getStartSliceLocation()
                            && ctSerie.getEndSliceLocation() < baseSerie.getEndSliceLocation()) {
                            log.info("study {} has repeated series", ctSerie.getLocalStudyKey());
                            if(log.isDebugEnabled()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    log.debug("base serie: {}", objectMapper.writeValueAsString(baseSerie));
                                    log.debug("current serie: {}", objectMapper.writeValueAsString(ctSerie));
                                } catch(Exception ex) {
                                }
                            }
                            baseSerie.setIsRepeated(Boolean.TRUE);
                            ctSerie.setIsRepeated(Boolean.TRUE);
                            series2Update.add(baseSerie);
                            series2Update.add(ctSerie);
                            hasRepeatedSeries = Boolean.TRUE;
                        }
                    }
                }
            }
            if(series2Update.size() > 1) {
                ctSerieRepository.saveAll(series2Update);
            }
        }
        return hasRepeatedSeries;
    }

    /**
     * As some studies have been persisted to database in previous job, to avoid values of study been
     * overwritten, need to merge studies from job and database.
     * @param studiesFromJob
     * @return merged studies
     * @since v1.1
     */
    private Set<Study> mergeStudies(Set<Study> studiesFromJob) {
        List<String> studyIds = studiesFromJob.stream().map(s -> s.getLocalStudyId()).collect(Collectors.toList());
        List<Study> studyFromDB = studyRepository.findByLocalStudyIdIn(studyIds);
        studiesFromJob.stream().filter(s -> !studyFromDB.contains(s)).forEach(studyFromDB::add);
        return new HashSet<>(studyFromDB);
    }

    /**
     * retrieve all ct series belongs to studies from database
     * @param studySet
     * @return a Map, local study id as key, and series belong to the study
     * @since v1.1
     */
    private Map<String, TreeSet<CTSerie>> buildSeriesMap(Set<Study> studySet) {
        Map<String, TreeSet<CTSerie>> studyWithSeriesMap = new HashMap<>(studySet.size());
        List<String> studyIds = studySet.stream().map(s -> s.getLocalStudyId()).collect(Collectors.toList());
        List<CTSerie> ctSeriesFromDB = ctSerieRepository.findByLocalStudyKeyIn(studyIds);
        for(CTSerie ctse : ctSeriesFromDB) {
            TreeSet<CTSerie> ctSeries = studyWithSeriesMap.get(ctse.getLocalStudyKey());
            if(ctSeries == null) {
                ctSeries = new TreeSet<>();
            }
            ctSeries.add(ctse);
            studyWithSeriesMap.put(ctse.getLocalStudyKey(), ctSeries);
        }
        return studyWithSeriesMap;
    }
}
