/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.RisExam;
import cn.gehc.cpm.domain.RisReportToExam;
import cn.gehc.cpm.repository.RisExamRepository;
import cn.gehc.cpm.repository.RisReportToExamRepository;
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
 * @author 212579464
 */
@Service
public class RisReportToExamPullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(RisReportToExamPullJob.class);

    @Autowired
    private RisReportToExamRepository reportToExamRepository;

    @Autowired
    private RisExamRepository risExamRepository;

    public void insertBatchExam(@Headers Map<String, Object> headers, @Body Object body) {

        List<Map<String, Object>> examMap = (List<Map<String, Object>>) body;

        Date maxDate = saveData2DB(examMap);

        //记录读取进度
        if (maxDate != null) {
            super.updateLastPullValue(headers, DateTimeUtil.getStrDate(maxDate, "yyyyMMddHHmmss"));
        }
    }

    private Date saveData2DB(List<Map<String, Object>> data) {
        List<RisReportToExam> infos = new ArrayList<>(100);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        RisReportToExam info;
        StringBuilder warningMsg;
        Date maxDate = null;
        for(Map<String, Object> obj : data) {
            warningMsg = new StringBuilder("Empty field: ");
            info = new RisReportToExam();

            if(DataConvertUtils.isBlankField(obj.get("RequisitionID"))) {
                warningMsg.append("RequisitionID,");
            } else {
                info.setRequisitionId(String.valueOf(obj.get("RequisitionID")));
            }

            if(DataConvertUtils.isBlankField(obj.get("SheetID"))) {
                warningMsg.append("SheetID,");
            } else {
                info.setSheetId(String.valueOf(obj.get("SheetID")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ReportToDateTime"))) {
                warningMsg.append("ReportToDateTime,");
            } else {
                try {
                    info.setReportToDateTime(
                            dateFormat.parse(String.valueOf(obj.get("ReportToDateTime")))
                    );

                    // get max date
                    if(maxDate == null) {
                        maxDate = info.getReportToDateTime();
                    } else {
                        maxDate = (maxDate.compareTo(info.getReportToDateTime()) > 0) ? maxDate : info.getReportToDateTime();
                    }
                } catch (Exception ex) {
                    log.warn("RequisitionId: " + info.getRequisitionId() + ", parse reportToDateTime failed.");
                }
            }

            if(DataConvertUtils.isBlankField(obj.get("PatientID"))) {
                warningMsg.append("PatientID,");
            } else {
                info.setPatientId(String.valueOf(obj.get("PatientID")));
            }

            if(DataConvertUtils.isBlankField(obj.get("AccessionNumber"))) {
                warningMsg.append("AccessionNumber,");
            } else {
                info.setAccessionNumber(String.valueOf(obj.get("AccessionNumber")));
            }

            infos.add(info);
        }
        Iterable<RisReportToExam> result = reportToExamRepository.saveAll(infos);

        saveData2RisExam(infos);

        return maxDate;
    }

    private void saveData2RisExam(List<RisReportToExam> infos) {
        Map<String, RisReportToExam> infoMap = new HashMap<>(100);
        List<String> requisitionIds = new ArrayList<>(100);
        List<RisExam> risExams2Sync = new ArrayList<>(100);

        infos.forEach(info -> {
            if(info.getRequisitionId() != null) {
                requisitionIds.add(info.getRequisitionId().trim());
                infoMap.put(info.getRequisitionId().trim(), info);
            }
        });

        List<RisExam> risExamList;
        if(requisitionIds.size() > 0) {
            risExamList = risExamRepository.findByRequisitionIdIn(requisitionIds);
            risExamList.forEach(risExam -> {
                RisReportToExam info = infoMap.remove(risExam.getRequisitionId());
                risExams2Sync.add(populateInfo2RisExam(info, risExam));
            });
            log.info("{} records will be updated", risExams2Sync.size());
        }

        RisExam risExam;
        for(RisReportToExam info : infoMap.values()) {
            if(info.getSheetId() != null) {
                risExam = new RisExam();
                risExam.setRequisitionId(info.getRequisitionId());
                risExams2Sync.add(populateInfo2RisExam(info, risExam));
            } else {
                //do nothing here
            }
        }
        risExamRepository.saveAll(risExams2Sync);
    }

    private RisExam populateInfo2RisExam(RisReportToExam info, RisExam risExam) {
        risExam.setSheetId(info.getSheetId());
        risExam.setPreRegisteDate(info.getReportToDateTime());
        risExam.setPatientId(info.getPatientId());
        risExam.setAccessionNum(info.getAccessionNumber());

        return risExam;
    }
}
