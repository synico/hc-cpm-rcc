package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.*;
import cn.gehc.cpm.process.xa.StudyDurationProcess;
import cn.gehc.cpm.process.xa.XAStudyProtocolProcess;
import cn.gehc.cpm.repository.XASerieRepository;
import cn.gehc.cpm.repository.XAStudyRepository;
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

@Service(value = "xaSeriePullJob")
public class XASeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(XASeriePullJob.class);

    @Autowired
    private XAStudyRepository xaStudyRepository;

    @Autowired
    private XASerieRepository xaSerieRepository;

    @Autowired
    private StudyDurationProcess studyDurationProcess;

    @Autowired
    private XAStudyProtocolProcess xaStudyProtocolProcess;

    @Override
    public void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to insert data to xa_serie, [ {} ] records will be processed", body.size());

        Set<Study> studiesFromJob = new HashSet<>();
        Set<XAStudy> xaStudySet = new HashSet<>();
        Set<XASerie> xaSerieSet = new HashSet<>();

        Map<String, Set<XASerie>> studyWithSerieMap;

        Long lastPolledValue = null;
        Study study;
        XAStudy xaStudy;
        XASerie xaSerie;
        Long orgId = 0L;
        // save study, xa_study, xa_serie
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

            xaStudy = DataUtil.convertProps2XAStudy(serieProps);
            xaStudySet.add(xaStudy);

            xaSerie = DataUtil.convertProps2XASerie(serieProps);
            xaSerieSet.add(xaSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            if (lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }
        }

        if (!xaStudySet.isEmpty()) {
            xaStudyRepository.saveAll(xaStudySet);
        }

        if (!xaSerieSet.isEmpty()) {
            xaSerieRepository.saveAll(xaSerieSet);
        }

        // since v1.1
        Set<Study> mergedStudies = super.mergeStudies(studiesFromJob);
        studyWithSerieMap = this.buildSeriesMap(mergedStudies);
        studyDurationProcess.process(mergedStudies, studyWithSerieMap);

        xaStudyProtocolProcess.process(xaStudySet, studyWithSerieMap);

        if (!xaStudySet.isEmpty()) {
            xaStudyRepository.saveAll(xaStudySet);
        }

        if (!mergedStudies.isEmpty()) {
            studyRepository.saveAll(mergedStudies);
        }

        log.info("[ {} ] studies have been saved, [ {} ] xa studies have been saved, [ {} ] xa series have been saved",
                mergedStudies.size(), xaStudySet.size(), xaSerieSet.size());

        linkStudies(mergedStudies);

        if (lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }

    /**
     * retrieve all xa series belongs to studies from database
     * @param studySet
     * @return a Map, local study id as key, and series belong to the study
     * @since v1.1
     */
    private Map<String, Set<XASerie>> buildSeriesMap(Set<Study> studySet) {
        Map<String, Set<XASerie>> studyWithSeriesMap = new HashMap<>(studySet.size());
        List<String> studyIds = studySet.stream().map(s -> s.getLocalStudyId()).collect(Collectors.toList());
        List<XASerie> xaSeriesFromDb = xaSerieRepository.findByLocalStudyKeyIn(studyIds);
        for (XASerie xase : xaSeriesFromDb) {
            Set<XASerie> xaSeries = studyWithSeriesMap.get(xase.getLocalStudyKey());
            if (xaSeries == null) {
                xaSeries = new TreeSet<>();
            }
            xaSeries.add(xase);
            studyWithSeriesMap.put(xase.getLocalStudyKey(), xaSeries);
        }
        return studyWithSeriesMap;
    }

}
