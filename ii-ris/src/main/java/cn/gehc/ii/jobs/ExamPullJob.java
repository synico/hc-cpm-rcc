package cn.gehc.ii.jobs;

import cn.gehc.ii.domain.NisExam;
import cn.gehc.ii.repository.ExamRepository;
import cn.gehc.ii.util.DataUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 212706300
 */

@Service("examPullJob")
public class ExamPullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(ExamPullJob.class);

    @Autowired
    private ExamRepository examRepository;

    public synchronized void processData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to process exams, [ {} ] records will be processed", body.size());

        List examList = new ArrayList(body.size());
        Date maxDateInBatch = null;

        for (Map<String, Object> examProps : body) {
            NisExam exam = DataUtil.convertRow2Exam(examProps);
            log.debug("sheetid: [ {} ] is being processed, last update date: [ {} ]", exam.getNisExamKey(), exam.getLastUpdateDate());
            examList.add(exam);
            if (maxDateInBatch != null) {
                maxDateInBatch = maxDateInBatch.compareTo(exam.getLastUpdateDate()) > 0 ? maxDateInBatch : exam.getLastUpdateDate();
            } else {
                maxDateInBatch = exam.getLastUpdateDate();
            }
        }

        if(examList.isEmpty()) {
            log.info("There is no exam, nothing will be saved");
        } else {
            log.info("[ {} ] exams will be saved", examList.size());
            examRepository.saveAll(examList);
        }

        if (maxDateInBatch != null) {
            log.info("max date in batch: [ {} ]", maxDateInBatch);
            super.updateLastPullValue(headers, DataUtil.convertDate2String(maxDateInBatch, "yyyy-MM-dd HH:mm:ss"));
        }

        log.info("end to process data to exam, [ {} ] records have been saved", examList.size());
    }

}
