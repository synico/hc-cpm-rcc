package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.HisExam;
import cn.gehc.cpm.domain.ReqCancelScheduleInfo;
import cn.gehc.cpm.repository.HisExamRepository;
import cn.gehc.cpm.repository.ReqCancelScheduleInfoRepository;
import cn.gehc.cpm.utils.DataConvertUtils;
import cn.gehc.cpm.utils.DateTimeUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReqCancelScheduleInfoPullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(ReqCancelScheduleInfoPullJob.class);

    @Autowired
    ReqCancelScheduleInfoRepository cancelScheduleInfoRepository;

    @Autowired
    HisExamRepository hisExamRepository;

    public void insertBatchExam(@Headers Map<String, Object> headers, @Body Object body) {

        List<Map<String, Object>> examMap = (List<Map<String, Object>>) body;

        Date maxDate = saveData2DB(examMap);

        //记录读取进度
        if (maxDate != null) {
            super.updateLastPullValue(headers, DateTimeUtil.getStrDate(maxDate, "yyyyMMdd HHmmss"));
        }
    }

    /**
     * @param data
     * @return max datetime of the data for recording current progress
     */
    private Date saveData2DB(List<Map<String, Object>> data) {
        List<ReqCancelScheduleInfo> infos = new ArrayList<>(100);
        SimpleDateFormat reqDataFormatter = new SimpleDateFormat("yyyyMMdd HHmmss");
        ReqCancelScheduleInfo info;
        Date maxDate = null;
        StringBuilder warningMsg;
        for(Map<String, Object> obj : data) {
            warningMsg = new StringBuilder("Empty field: ");
            info = new ReqCancelScheduleInfo();

            infos.add(info);

            if(DataConvertUtils.isBlankField(obj.get("SheetID"))) {
                warningMsg.append("SheetID,");
            } else {
                info.setSheetId(String.valueOf(obj.get("SheetId")));
                info.setIsCanceled(Boolean.TRUE);
            }

            if(DataConvertUtils.isNotBlankField(obj.get("CancelScheduleDate")) && DataConvertUtils.isNotBlankField(obj.get("CancelScheduleTime"))) {
                info.setCancelScheduleDate(String.valueOf(obj.get("CancelScheduleDate")));
                info.setCancelScheduleTime(String.valueOf(obj.get("CancelScheduleTime")));
                String cancelScheduleDatetime = obj.get("CancelScheduleDate") + " " + obj.get("CancelScheduleTime");
                try {
                    Date cancelDatetime = reqDataFormatter.parse(cancelScheduleDatetime);
                    info.setPreExamScheduleCancelDate(cancelDatetime);
                    if(maxDate == null) {
                        maxDate = cancelDatetime;
                    } else {
                        maxDate = (maxDate.compareTo(cancelDatetime) > 0) ? maxDate : cancelDatetime;
                    }
                } catch (Exception ex) {
                    log.warn("SheetId: " + info.getSheetId() + ", parse cancelDateTime failed");
                }
            } else {
                warningMsg.append("CancelScheduleDate,CancelScheduleTime");
            }

            if(log.isWarnEnabled() && warningMsg.toString().endsWith(",")) {
                log.warn(warningMsg.toString());
            }
        }

        Iterable<ReqCancelScheduleInfo> result = cancelScheduleInfoRepository.saveAll(infos);

        saveData2HisExam(infos);

        return maxDate;
    }

    private void saveData2HisExam(List<ReqCancelScheduleInfo> infos) {
        Map<String, ReqCancelScheduleInfo> infoMap = new HashMap<>(100);
        List<String> sheetids = new ArrayList<>(100);
        List<HisExam> hisExams2Sync = new ArrayList<>(100);

        infos.forEach(info -> {
            infoMap.put(info.getSheetId(), info);
            sheetids.add(info.getSheetId());
        });

        List<HisExam> hisExamList = hisExamRepository.findBySheetIdIn(sheetids);
        hisExamList.forEach(hisExam -> {
            ReqCancelScheduleInfo info = infoMap.remove(hisExam.getSheetId());
            hisExam.setIsScheduleCanceled(Boolean.TRUE);
            hisExams2Sync.add(hisExam);
        });
        log.info("{} records will be updated", hisExams2Sync.size());

        HisExam hisExam;
        for(ReqCancelScheduleInfo info : infoMap.values()) {
            hisExam = new HisExam();
            hisExam.setSheetId(info.getSheetId());
            hisExam.setIsScheduleCanceled(Boolean.TRUE);
            hisExams2Sync.add(hisExam);
        }
        hisExamRepository.saveAll(hisExams2Sync);
    }

}
