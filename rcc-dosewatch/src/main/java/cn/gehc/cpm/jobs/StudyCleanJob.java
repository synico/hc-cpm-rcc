package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.DeviceKey;
import cn.gehc.cpm.domain.OrgEntity;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.util.DataUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service(value = "studyCleanJob")
public class StudyCleanJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(StudyCleanJob.class);

    public void cleanStudies(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to clean studies, [ {} ] records will be processed", body.size());

        //Do nothing, if there isn't data retrieved from database
        if (body.isEmpty()) {
            log.info("study clean job: nothing has been fetched from database");
            return;
        }

        Set<String> localStudyIdsInDW = new HashSet<>();
        Set<DeviceKey> aeKeys = new HashSet<>();
        Map<DeviceKey, Set<String>> aeStudyDateMap = new HashMap<>();

        String localStudyId;
        Long orgId = 0L;
        DeviceKey deviceKey;
        String studyDateStr;
        for (Map<String, Object> studiesInDW : body) {
            log.debug(studiesInDW.toString());

            // retrieve org entity id by facility code
            String facilityCode = DataUtil.getStringFromProperties(studiesInDW, "facility_code");
            if (orgId.longValue() == 0 && StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                if (orgEntityList.size() > 0) {
                    orgId = orgEntityList.get(0).getOrgId();
                }
                log.info("facility {} is retrieved", orgId);
            }
            if (orgId.longValue() == 0 && StringUtils.isBlank(facilityCode)) {
                log.error("facility hasn't been configured for aet: [ {} ]", DataUtil.getStringFromProperties(studiesInDW, "aet"));
                continue;
            }

            deviceKey = new DeviceKey();
            deviceKey.of(orgId,
                    DataUtil.getStringFromProperties(studiesInDW, "aet"),
                    DataUtil.getStringFromProperties(studiesInDW, "modality"));
            aeKeys.add(deviceKey);

            studyDateStr = DataUtil.getStringFromProperties(studiesInDW, "study_date_str");
            Set<String> studyDateSet;
            if (aeStudyDateMap.containsKey(deviceKey)) {
                studyDateSet = aeStudyDateMap.get(deviceKey);
                studyDateSet.add(studyDateStr);
            } else {
                studyDateSet = new HashSet<>();
                studyDateSet.add(studyDateStr);
                aeStudyDateMap.put(deviceKey, studyDateSet);
            }

            localStudyId = deviceKey.toString() + "|" + DataUtil.getStringFromProperties(studiesInDW, "dw_study_id");

            localStudyIdsInDW.add(localStudyId);
        }

        List<Study> localStudies;
        List<Study> studies2Delete = new ArrayList<>();
        for (DeviceKey aeKey : aeKeys) {
            Set<String> studyDateSet = aeStudyDateMap.get(aeKey);
            for (String dateStr : studyDateSet) {
                localStudies = studyRepository.findByAEAndStudyDateChar(aeKey.getOrgId(),
                    aeKey.getAet(),
                    aeKey.getDeviceType(),
                    dateStr);
                for (Study study : localStudies) {
                    log.debug("study: [ {} ] and published: [ {} ]", study.getLocalStudyId(), study.getPublished());
                    if (localStudyIdsInDW.contains(study.getLocalStudyId())) {
                        //do nothing
                    } else {
                        log.info("study will be marked to deletion: [ {} ]", study.getLocalStudyId());
                        study.setPublished(Study.StudyStatus.MARK_FOR_DELETION.getStatusId());
                        studies2Delete.add(study);
                    }
                }
            }
        }

        if (!studies2Delete.isEmpty()) {
            studyRepository.saveAll(studies2Delete);
        }
    }

    @Override
    public void insertData(Map<String, Object> headers, List<Map<String, Object>> body) {

    }
}
