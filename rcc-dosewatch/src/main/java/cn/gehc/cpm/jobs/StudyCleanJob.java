package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.OrgEntity;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.OrgEntityRepository;
import cn.gehc.cpm.repository.StudyRepository;
import cn.gehc.cpm.util.DataUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service(value = "studyCleanJob")
public class StudyCleanJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(StudyCleanJob.class);

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private OrgEntityRepository orgEntityRepository;

    public void cleanStudies(@Headers Map<String, Object> headers, @Body Object body) {
        log.info("start to clean studies");
        List<Map<String, Object>> dataMap = (List<Map<String, Object>>) body;

        //Do nothing, if there isn't data selected from database
        if(dataMap.size() == 0) {
            return;
        }

        Set<String> localStudyIdsInDW = new HashSet<>();
        Set<String> aets = new HashSet<>();

        LocalDate todayDate = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayStr = dateTimeFormatter.format(todayDate);

        String localStudyId;
        String aet;
        String modality;
        Long orgId = 0L;
        for(Map<String, Object> studiesInDW : dataMap) {
            log.debug(studiesInDW.toString());

            // retrieve org entity id by facility code
            String facilityCode = DataUtil.getStringFromProperties(studiesInDW, "facility_code");
            if(orgId.longValue() == 0 && StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                if(orgEntityList.size() > 0) {
                    orgId = orgEntityList.get(0).getOrgId();
                }
                log.info("facility {} is retrieved", orgId);
            }
            if(orgId.longValue() == 0 && StringUtils.isBlank(facilityCode)) {
                log.error("facility hasn't been configured for aet: {}", DataUtil.getStringFromProperties(studiesInDW, "aet"));
                continue;
            }

            aet = DataUtil.getStringFromProperties(studiesInDW, "aet");
            modality = DataUtil.getStringFromProperties(studiesInDW, "modality");
            localStudyId = orgId + "|" + DataUtil.getStringFromProperties(studiesInDW, "local_study_id");
            aets.add(aet);
            localStudyIdsInDW.add(localStudyId);

        }

        List<Study> localStudies = studyRepository.findByAETsAndStudyDateChar(aets, todayStr);
        List<Study> studies2Delete = new ArrayList<>();
        for(Study study : localStudies) {
            log.debug("study: {} and published: {}", study.getLocalStudyId(), study.getPublished());
            if(localStudyIdsInDW.contains(study.getLocalStudyId())) {
                //do nothing
            } else {
                log.info("study will be marked to deletion: {}", study.getLocalStudyId());
                study.setPublished(Study.StudyStatus.MARK_FOR_DELETION.getStatusId());
                studies2Delete.add(study);
            }
        }

        if(studies2Delete.size() > 0) {
            studyRepository.saveAll(studies2Delete);
        }
    }

}
