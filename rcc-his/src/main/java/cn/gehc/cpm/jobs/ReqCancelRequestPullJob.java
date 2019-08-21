/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.HisExam;
import cn.gehc.cpm.domain.ReqCancelRequest;
import cn.gehc.cpm.repository.HisExamRepository;
import cn.gehc.cpm.repository.ReqCancelRequestRepository;
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

/**
 *
 * @author 212706300
 */
@Service
public class ReqCancelRequestPullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(ReqCancelRequestPullJob.class);

    @Autowired
    private ReqCancelRequestRepository cancelRequestRepository;

    @Autowired
    private HisExamRepository hisExamRepository;

    public void insertBatchExam(@Headers Map<String, Object> headers, @Body Object body) {

        List<Map<String, Object>> examMap = (List<Map<String, Object>>) body;

        Date maxDate = saveData2DB(examMap);
        
        //记录读取进度
        if(maxDate != null){
            super.updateLastPullValue(headers, DateTimeUtil.getStrDate(maxDate, "yyyyMMdd HHmmss"));
        }
    }

    /**
     * @param data
     * @return max datetime of the data for recording current progress
     */
    private Date saveData2DB(List<Map<String, Object>> data) {
        List<ReqCancelRequest> infos = new ArrayList<>(100);
        SimpleDateFormat cancelDateFormatter = new SimpleDateFormat("yyyyMMdd HHmmss");
        ReqCancelRequest info;
        Date maxDate = null;
        StringBuilder warningMsg;
        for(Map<String, Object> obj : data) {
            warningMsg = new StringBuilder("Empty field: ");
            info = new ReqCancelRequest();

            if(DataConvertUtils.isBlankField(obj.get("SheetID"))) {
                warningMsg.append("SheetID,");
            } else {
                info.setSheetId(String.valueOf(obj.get("SheetID")));
                info.setIsCanceled(Boolean.TRUE);
            }

            if(DataConvertUtils.isBlankField(obj.get("ASCancelExamDateTime"))) {
                warningMsg.append("ASCancelExamDateTime,");
            } else {
                try {
                    info.setAsCancelExamDateTime(
                            cancelDateFormatter.parse(String.valueOf(obj.get("ASCancelExamDateTime")))
                    );

                    if(maxDate == null) {
                        maxDate = info.getAsCancelExamDateTime();
                    } else {
                        maxDate = (maxDate.compareTo(info.getAsCancelExamDateTime()) > 0) ? maxDate : info.getAsCancelExamDateTime();
                    }
                } catch (Exception ex) {
                    log.warn("SheetId: " + info.getSheetId() + ", parse ASCancelExamDateTime failed.");
                }
            }

            info.setCancelExamDoctor(String.valueOf(obj.get("CancelExamDoctor")));

            infos.add(info);

            if(log.isWarnEnabled() && warningMsg.toString().endsWith(",")) {
                log.warn(warningMsg.toString());
            }
        }

        Iterable<ReqCancelRequest> result = cancelRequestRepository.saveAll(infos);

        saveData2HisExam(infos);

        return maxDate;
    }

    private void saveData2HisExam(List<ReqCancelRequest> infos) {
        Map<String, ReqCancelRequest> infoMap = new HashMap<>(100);
        List<String> sheetids = new ArrayList<>(100);
        List<HisExam> hisExams2Sync = new ArrayList<>(100);

        infos.forEach(info -> {
            sheetids.add(info.getSheetId().trim());
            infoMap.put(info.getSheetId().trim(), info);
        });

        List<HisExam> hisExamList = hisExamRepository.findBySheetIdIn(sheetids);
        hisExamList.forEach(hisExam -> {
            ReqCancelRequest info = infoMap.remove(hisExam.getSheetId());
            hisExam.setIsCanceled(Boolean.TRUE);
            hisExam.setExamCancelDate(info.getAsCancelExamDateTime());
            hisExams2Sync.add(hisExam);
        });
        log.info("{} records will be updated", hisExams2Sync.size());

        HisExam hisExam;
        for(ReqCancelRequest req : infoMap.values()) {
            hisExam = new HisExam();
            hisExam.setSheetId(req.getSheetId());
            hisExam.setIsCanceled(Boolean.TRUE);
            hisExam.setExamCancelDate(req.getAsCancelExamDateTime());
            hisExams2Sync.add(hisExam);
        }
        hisExamRepository.saveAll(hisExams2Sync);
    }

}
