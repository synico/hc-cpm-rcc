/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.RisExam;
import cn.gehc.cpm.domain.RisReportFirst;
import cn.gehc.cpm.repository.RisExamRepository;
import cn.gehc.cpm.repository.RisReportFirstRepository;
import cn.gehc.cpm.utils.DataConvertUtils;
import cn.gehc.cpm.utils.DateTimeUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
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
public class RisReportFirstPullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(RisReportFirstPullJob.class);

    @Autowired
    private RisReportFirstRepository reportFirstRepository;

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

    /**
     * @param data
     * @return max datetime of the data for recording current progress
     */
    private Date saveData2DB(List<Map<String, Object>> data) {
        List<RisReportFirst> reportList = new ArrayList<>(100);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        RisReportFirst report;
        Date maxDate = null;
        StringBuilder warningMsg;
        for(Map<String, Object> obj : data) {
            warningMsg = new StringBuilder("Empty field: ");
            report = new RisReportFirst();

            if(DataConvertUtils.isBlankField(obj.get("RequisitionID"))) {
                warningMsg.append("RequisitionID,");
            } else {
                report.setRequisitionId(String.valueOf(obj.get("RequisitionID")));
            }

            if(DataConvertUtils.isBlankField(obj.get("HISsheetid"))) {
                warningMsg.append("HISsheetid,");
            } else {
                report.setHisSheetid(String.valueOf(obj.get("HISsheetid")));
            }

            if(DataConvertUtils.isBlankField(obj.get("HISUserID"))) {
                warningMsg.append("HISUserID,");
            } else {
                report.setHisUserId(String.valueOf(obj.get("HISUserID")));
            }

            if(DataConvertUtils.isBlankField(obj.get("UserName"))) {
                warningMsg.append("UserName,");
            } else {
                report.setUserName(String.valueOf(obj.get("UserName")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ActionDateTime"))) {
                warningMsg.append("ActionDateTime,");
            } else {
                try {
                    report.setActionDateTime(
                            dateFormat.parse(String.valueOf(obj.get("ActionDateTime")))
                    );

                    if(maxDate == null) {
                        maxDate = report.getActionDateTime();
                    } else {
                        maxDate = (maxDate.compareTo(report.getActionDateTime()) > 0) ? maxDate : report.getActionDateTime();
                    }
                } catch (Exception ex) {
                    log.warn("RequisitionId: " + report.getRequisitionId() + ", parse actionDateTime failed.");
                }
            }

            report.setPositiveFlag(Integer.parseInt(String.valueOf(obj.get("PositiveFlag"))));
            report.setReportState(Integer.parseInt(String.valueOf(obj.get("ReportState"))));

            reportList.add(report);

            if(log.isWarnEnabled() && warningMsg.toString().endsWith(",")) {
                log.warn(warningMsg.toString());
            }
        }

        Iterable<RisReportFirst> result = reportFirstRepository.saveAll(reportList);

        saveData2RisExam(reportList);

        return maxDate;
    }

    private void saveData2RisExam(List<RisReportFirst> infos) {
        Map<String, RisReportFirst> infoMap = new HashMap<>(100);
        List<String> requisitionIds = new ArrayList<>(100);
        List<RisExam> risExams2Sync = new ArrayList<>(100);

        infos.forEach(info -> {
            requisitionIds.add(info.getRequisitionId());
            infoMap.put(info.getRequisitionId().trim(), info);
        });

        List<RisExam> risExamList = risExamRepository.findByRequisitionIdIn(requisitionIds);
        risExamList.forEach(risExam -> {
            RisReportFirst info = infoMap.remove(risExam.getRequisitionId());
            risExams2Sync.add(populateInfo2RisExam(info, risExam));
        });
        log.info("{} records will be updated", risExams2Sync.size());

        RisExam risExam;
        for(RisReportFirst info : infoMap.values()) {
            if(info.getRequisitionId() != null) {
                risExam = new RisExam();
                risExam.setRequisitionId(info.getRequisitionId());
                risExams2Sync.add(populateInfo2RisExam(info, risExam));
            } else {
                //do nothing here
            }
        }
        risExamRepository.saveAll(risExams2Sync);
    }

    private RisExam populateInfo2RisExam(RisReportFirst info, RisExam risExam) {
        risExam.setSheetId(info.getHisSheetid());
        risExam.setPostExamReporterId(info.getHisUserId());
        risExam.setPostExamReporterName(info.getUserName());
        risExam.setSubmitReportTime(info.getActionDateTime());
        if(StringUtils.isBlank(risExam.getExamResult())) {
            risExam.setExamResult(info.getPositiveFlag().toString());
        }

        return risExam;
    }

}
