package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.CTStudy;
import cn.gehc.cpm.domain.OrgEntity;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.process.ct.RepeatSeriesCheckProcess;
import cn.gehc.cpm.process.ct.StudyDurationProcess;
import cn.gehc.cpm.process.ct.TargetRegionCountProcess;
import cn.gehc.cpm.repository.CTSerieRepository;
import cn.gehc.cpm.repository.CTStudyRepository;
import cn.gehc.cpm.util.DataUtil;
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

    @Autowired
    private RepeatSeriesCheckProcess repeatSeriesCheckProcess;

    @Override
    public void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to insert/update data to ct_serie, [ {} ] records will be processed", body.size());

        Set<Study> studiesFromJob = new HashSet<>();
        Set<CTStudy> ctStudySet = new HashSet<>();
        Set<CTSerie> ctSerieSet = new HashSet<>();

        Map<String, Set<CTSerie>> studyWithSeriesMap;

        Long lastPolledValue = null;
        Study study;
        CTStudy ctStudy;
        CTSerie ctSerie;
        Long orgId = 0L;
        Boolean studyProcessedByRDSR = "RDSR".equals(headers.get("StudyProcessMethod")) ? Boolean.TRUE : Boolean.FALSE;
        if (studyProcessedByRDSR) {
            log.info("******studies will be processed by RDSR method******");
        }
        // save study, ct_study, ct_serie
        for (Map<String, Object> serieProps : body) {
            log.debug(serieProps.toString());

            // retrieve org entity id by facility code
            String facilityCode = DataUtil.getStringFromProperties(serieProps, "facility_code");
            if (orgId.longValue() == 0 && StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                if (orgEntityList.isEmpty()) {
                    // !!! IMPORTANT !!! job will not save data to database since org_entity has not been properly configured
                    log.warn("The org/device has not been synchronized, job will not save data");
                    return;
                } else {
                    orgId = orgEntityList.get(0).getOrgId();
                    log.info("facility < {} > has been retrieved", orgId);
                }
            }
            if (orgId.longValue() == 0 && StringUtils.isBlank(facilityCode)) {
                log.error("facility hasn't been configured for aet: < {} >", DataUtil.getStringFromProperties(serieProps, "aet"));
                continue;
            }
            serieProps.put("org_id", orgId);
            serieProps.put("study_processed_by_rdsr", studyProcessedByRDSR);

            study = DataUtil.convertProps2Study(serieProps);
            studiesFromJob.add(study);

            ctStudy = DataUtil.convertProps2CTStudy(serieProps);
            ctStudySet.add(ctStudy);

            ctSerie = DataUtil.convertProps2CTSerie(serieProps);
            ctSerieSet.add(ctSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            if (lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }
        }

        if (!ctStudySet.isEmpty()) {
            ctStudyRepository.saveAll(ctStudySet);
        }

        if (!ctSerieSet.isEmpty()) {
            ctSerieRepository.saveAll(ctSerieSet);
        }

        // since v1.1
        Set<Study> mergedStudies = super.mergeStudies(studiesFromJob);
        studyWithSeriesMap = this.buildSeriesMap(mergedStudies, !studyProcessedByRDSR);
        if (studyProcessedByRDSR) {
            // infer study duration by RDSR, instead of sorted series
        } else {
            studyDurationProcess.process(mergedStudies, studyWithSeriesMap);
        }
        targetRegionCountProcess.process(mergedStudies, studyWithSeriesMap);
        repeatSeriesCheckProcess.process(mergedStudies, studyWithSeriesMap);

        if (!mergedStudies.isEmpty()) {
            studyRepository.saveAll(mergedStudies);
        }

        log.info("[ {} ] studies have been saved, [ {} ] ct studies have been saved, [ {} ] ct series have been saved",
                mergedStudies.size(), ctStudySet.size(), ctSerieSet.size());

        linkStudies(mergedStudies);

        if (lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }

    /**
     * retrieve all ct series belongs to studies from database
     * @param studySet
     * @return a Map, local study id as key, and series belong to the study
     * @since v1.1
     */
    private Map<String, Set<CTSerie>> buildSeriesMap(Set<Study> studySet, Boolean sort) {
        Map<String, Set<CTSerie>> studyWithSeriesMap = new HashMap<>(studySet.size());
        List<String> studyIds = studySet.stream().map(s -> s.getLocalStudyId()).collect(Collectors.toList());
        List<CTSerie> ctSeriesFromDb = ctSerieRepository.findByLocalStudyKeyIn(studyIds);
        for (CTSerie ctse : ctSeriesFromDb) {
            Set<CTSerie> ctSeries = studyWithSeriesMap.get(ctse.getLocalStudyKey());
            if (ctSeries == null) {
                ctSeries = sort ? new TreeSet<>() : new HashSet<>();
            }
            ctSeries.add(ctse);
            studyWithSeriesMap.put(ctse.getLocalStudyKey(), ctSeries);
        }
        return studyWithSeriesMap;
    }

}
