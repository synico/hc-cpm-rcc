package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.MRSerie;
import cn.gehc.cpm.domain.MRStudy;
import cn.gehc.cpm.domain.OrgEntity;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.process.mr.MRStudyProtocolProcess;
import cn.gehc.cpm.process.mr.RepeatSeriesCheckProcess;
import cn.gehc.cpm.process.mr.StudyDurationProcess;
import cn.gehc.cpm.process.mr.TargetRegionCountProcess;
import cn.gehc.cpm.repository.MRSerieRepository;
import cn.gehc.cpm.repository.MRStudyRepository;
import cn.gehc.cpm.util.DataUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 212706300
 */

@Service(value = "mrSeriePullJob")
public class MRSeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(MRSeriePullJob.class);

    @Autowired
    private MRStudyRepository mrStudyRepository;

    @Autowired
    private MRSerieRepository mrSerieRepository;

    @Autowired
    private StudyDurationProcess studyDurationProcess;

    @Autowired
    private TargetRegionCountProcess targetRegionCountProcess;

    @Autowired
    private RepeatSeriesCheckProcess repeatSeriesCheckProcess;

    @Autowired
    private MRStudyProtocolProcess mrStudyProtocolProcess;

    @Override
    public void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to insert data to mr_serie, [ {} ] records will be processed", body.size());

        Set<Study> studiesFromJob = new HashSet<>();
        Set<MRStudy> mrStudySet = new HashSet<>();
        Set<MRSerie> mrSerieSet = new HashSet<>();

        Map<String, Set<MRSerie>> studyWithSeriesMap;

        Long lastPolledValue = null;
        Study study;
        MRStudy mrStudy;
        MRSerie mrSerie;
        Long orgId = 0L;
        // save study, mr_study, mr_serie
        for (Map<String, Object> serieProps : body) {
            log.debug(serieProps.toString());

            // retrieve org entity id by facility code
            String facilityCode = DataUtil.getStringFromProperties(serieProps, "facility_code");
            if (orgId.longValue() == 0 && StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                if (orgEntityList.isEmpty()) {
                    // !!! IMPORTANT !!! job will not save data to database while org_entity has not been set
                    log.warn("The org/device has not been synchronized, job will not save data");
                    return;
                } else {
                    orgId = orgEntityList.get(0).getOrgId();
                    log.info("facility [ {} ] is retrieved", orgId);
                }
            }
            if (orgId.longValue() == 0 && StringUtils.isBlank(facilityCode)) {
                log.error("facility hasn't been configured for aet: [ {} ]", DataUtil.getStringFromProperties(serieProps, "aet"));
                continue;
            }
            serieProps.put("org_id", orgId);

            study = DataUtil.convertProps2Study(serieProps);
            studiesFromJob.add(study);

            mrStudy = DataUtil.convertProps2MRStudy(serieProps);
            mrStudySet.add(mrStudy);

            mrSerie = DataUtil.convertProps2MRSerie(serieProps);
            mrSerieSet.add(mrSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            if (lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }
        }

        if (!mrSerieSet.isEmpty()) {
            mrSerieRepository.saveAll(mrSerieSet);
        }

        // since v1.1
        Set<Study> mergedStudies = super.mergeStudies(studiesFromJob);
        studyWithSeriesMap = this.buildSeriesMap(mergedStudies);
        studyDurationProcess.process(mergedStudies, studyWithSeriesMap);
        targetRegionCountProcess.process(mergedStudies, studyWithSeriesMap);
        repeatSeriesCheckProcess.process(mergedStudies, studyWithSeriesMap);

        mrStudyProtocolProcess.process(mrStudySet, studyWithSeriesMap);

        if (!mrStudySet.isEmpty()) {
            mrStudyRepository.saveAll(mrStudySet);
        }

        if (!mergedStudies.isEmpty()) {
            studyRepository.saveAll(mergedStudies);
        }

        log.info("[ {} ] studies have been saved, [ {} ] mr studies have been saved, [ {} ] mr series have been saved",
                mergedStudies.size(), mrStudySet.size(), mrSerieSet.size());

        linkStudies(mergedStudies);

        if (lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }

    /**
     * retrieve all mr series belongs to studies from database
     * @param studySet
     * @return a Map, local study id as key, and series belong to the study
     * @since v1.1
     */
    private Map<String, Set<MRSerie>> buildSeriesMap(Set<Study> studySet) {
        Map<String, Set<MRSerie>> studyWithSeriesMap = new HashMap<>(studySet.size());
        List<String> studyIds = studySet.stream().map(s -> s.getLocalStudyId()).collect(Collectors.toList());
        List<MRSerie> ctSeriesFromDb = mrSerieRepository.findByLocalStudyKeyIn(studyIds);
        for (MRSerie mrse : ctSeriesFromDb) {
            Set<MRSerie> mrSeries = studyWithSeriesMap.get(mrse.getLocalStudyKey());
            if (mrSeries == null) {
                mrSeries = new TreeSet<>();
            }
            mrSeries.add(mrse);
            studyWithSeriesMap.put(mrse.getLocalStudyKey(), mrSeries);
        }
        return studyWithSeriesMap;
    }

}
