package cn.gehc.ii.jobs;

import cn.gehc.ii.domain.Exam;
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

    public enum JobType {
        BY_PRE_EXAM_DATE("pullExamByPreExamDateJob"),
        BY_EXAM_DATE("pullExamByExamDateJob");

        private String jobName;

        JobType(String jobName) {
            this.jobName = jobName;
        }

        public String getJobName() {
            return this.jobName;
        }
    }

    @Autowired
    private ExamRepository examRepository;

    public synchronized void processData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to process exams, [ {} ] records will be processed", body.size());
        Boolean processByPreExamDate = Boolean.TRUE;
        if (JobType.BY_EXAM_DATE.getJobName().equals(headers.get("JobName").toString())) {
            processByPreExamDate = Boolean.FALSE;
        }
        List examList = new ArrayList(body.size());
        Date maxDateInBatch = null;

        for (Map<String, Object> examProps : body) {
            Exam exam = DataUtil.convertRow2Exam(examProps);
            log.debug("requisition_id: [ {} ], sheetid: [ {} ] is being processed", exam.getRequisitionId(), exam.getSheetId());
            examList.add(exam);
            if (processByPreExamDate) {
                //
                if (maxDateInBatch == null) {
                    maxDateInBatch = exam.getPreExamDate();
                } else {
                    maxDateInBatch = maxDateInBatch.compareTo(exam.getPreExamDate()) > 0 ? maxDateInBatch : exam.getPreExamDate();
                }
            } else {
                if (maxDateInBatch == null) {
                    maxDateInBatch = exam.getExamDate();
                } else {
                    maxDateInBatch = maxDateInBatch.compareTo(exam.getExamDate()) > 0 ? maxDateInBatch : exam.getExamDate();
                }
            }
        }

        if(examList.isEmpty()) {
            log.info("There is no exam, nothing will be saved");
        } else {
            log.info("[ {} ] exams will be saved", examList.size());
            examRepository.saveAll(examList);
        }

        if (maxDateInBatch != null) {
            super.updateLastPullValue(headers, DataUtil.convertDate2String(maxDateInBatch, "yyyy-MM-dd HH:mm:ss"));
        }

        log.info("end to process data to exam, [ {} ] records have been saved", 1);
    }

}
