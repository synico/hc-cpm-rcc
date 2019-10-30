package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.StudyRepository;
import cn.gehc.cpm.util.DataUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
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
        for(Map<String, Object> studiesInDW : dataMap) {
            log.debug(studiesInDW.toString());
            aet = DataUtil.getStringFromProperties(studiesInDW, "aet");
            localStudyId = DataUtil.getStringFromProperties(studiesInDW, "local_study_id");
            aets.add(aet);
            localStudyIdsInDW.add(localStudyId);

        }

        List<Study> localStudies = studyRepository.findByAETsAndStudyDateChar(aets, todayStr);
        List<Study> studies2Delete = new ArrayList<>();
        for(Study study : localStudies) {
            log.info("study: {} and published: {}", study.getLocalStudyId(), study.getPublished());
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
