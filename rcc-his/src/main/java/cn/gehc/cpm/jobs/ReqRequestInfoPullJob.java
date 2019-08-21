/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.HisExam;
import cn.gehc.cpm.domain.ReqRequestInfo;
import cn.gehc.cpm.repository.HisExamRepository;
import cn.gehc.cpm.repository.ReqRequestInfoRepository;
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
public class ReqRequestInfoPullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(ReqRequestInfoPullJob.class);

    @Autowired
    private ReqRequestInfoRepository requestInfoRepository;

    @Autowired
    private HisExamRepository hisExamRepository;

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
        List<ReqRequestInfo> infos = new ArrayList<>(100);
        SimpleDateFormat birthFormatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat reqDataFormatter = new SimpleDateFormat("yyyyMMdd HHmmss");
        ReqRequestInfo info;
        Date maxDate = null;
        StringBuilder warningMsg;
        for(Map<String, Object> obj : data) {
            warningMsg = new StringBuilder("Empty field: ");
            info = new ReqRequestInfo();

            if(DataConvertUtils.isBlankField(obj.get("SheetID"))) {
                warningMsg.append("SheetID,");
            } else {
                info.setSheetId(String.valueOf(obj.get("SheetId")));
            }

            info.setReqSheetDoctorId(String.valueOf(obj.get("ReqSheetDoctorID")));
            info.setReqSheetDoctor(String.valueOf(obj.get("ReqSheetDoctor")));
            info.setReqDepartmentId(String.valueOf(obj.get("ReqDepartMentID")));

            if(DataConvertUtils.isBlankField(obj.get("ReqDepartMent"))) {
                warningMsg.append("ReqDepartMent,");
            } else {
                info.setReqDepartment(String.valueOf(obj.get("ReqDepartMent")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ReqDataTime"))) {
                warningMsg.append("ReqDataTime");
            } else {
                try {
                    info.setReqDataTime(
                            reqDataFormatter.parse(String.valueOf(obj.get("ReqDataTime")))
                    );

                    // get max date
                    if(maxDate == null) {
                        maxDate = info.getReqDataTime();
                    } else {
                        maxDate = (maxDate.compareTo(info.getReqDataTime()) > 0) ? maxDate : info.getReqDataTime();
                    }
                } catch (Exception ex) {
                    log.warn("SheetId: " + info.getSheetId() + ", parse ReqDataTime failed.");
                }
            }

            info.setOutHospitalId(String.valueOf(obj.get("OutHospitalID")));
            info.setInHospitalId(String.valueOf(obj.get("InHospitalID")));
            info.setPatientName(String.valueOf(obj.get("PatientName")));
            info.setPatientSex(String.valueOf(obj.get("PatientSex")));

            if(DataConvertUtils.isBlankField(obj.get("PatientBirth"))) {
                warningMsg.append("PatientBirth,");
            } else {
                try {
                    info.setPatientBirth(
                            birthFormatter.parse(String.valueOf(obj.get("PatientBirth")))
                    );
                } catch (Exception ex) {
                    log.warn("SheetId: " + info.getSheetId() + ",parse PatientBirth failed.");
                }
            }

            if(DataConvertUtils.isBlankField(obj.get("PatientID"))) {
                warningMsg.append("PatientID,");
            } else {
                info.setPatientId(String.valueOf(obj.get("PatientID")));
            }

            if(DataConvertUtils.isBlankField(obj.get("AccessionNum"))) {
                warningMsg.append("AccessionNum,");
            } else {
                info.setAccessionNum(String.valueOf(obj.get("AccessionNum")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ActualExamPlace"))) {
                warningMsg.append("ActualExamPlace,");
            } else {
                info.setActualExamPlace(String.valueOf(obj.get("ActualExamPlace")));
            }

            info.setExamKSDM(String.valueOf(obj.get("ExamKSDM")));
            info.setExamKSMC(String.valueOf(obj.get("ExamKSMC")));

            if(DataConvertUtils.isBlankField(obj.get("PatientStyle"))) {
                warningMsg.append("PatientStyle,");
            } else {
                info.setPatientStyle(String.valueOf(obj.get("PatientStyle")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ModalityRemark"))) {
                warningMsg.append("ModalityRemark,");
            } else {
                info.setModalityRemark(String.valueOf(obj.get("ModalityRemark")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ExamBodyPart"))) {
                warningMsg.append("ExamBodyPart,");
            } else {
                info.setExamBodyPart(String.valueOf(obj.get("ExamBodyPart")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ExamMethod"))) {
                warningMsg.append("ExamMethod");
            } else {
                info.setExamMethod(String.valueOf(obj.get("ExamMethod")));
            }

            infos.add(info);

            if(log.isWarnEnabled() && warningMsg.toString().endsWith(",")) {
                log.warn(warningMsg.toString());
            }
        }

        Iterable<ReqRequestInfo> result = requestInfoRepository.saveAll(infos);

        saveData2HisExam(infos);

        return maxDate;
    }

    private void saveData2HisExam(List<ReqRequestInfo> infos) {
        Map<String, ReqRequestInfo> infoMap = new HashMap<>(100);
        List<String> sheetids = new ArrayList<>(100);
        List<HisExam> hisExams2Sync = new ArrayList<>(100);

        infos.forEach(info -> {
            sheetids.add(info.getSheetId().trim());
            infoMap.put(info.getSheetId().trim(), info);
        });

        List<HisExam> hisExamList = hisExamRepository.findBySheetIdIn(sheetids);
        hisExamList.forEach(hisExam -> {
            ReqRequestInfo info = infoMap.remove(hisExam.getSheetId());
            hisExams2Sync.add(populateInfo2HisExam(info, hisExam));
        });
        log.info("{} records will be updated", hisExams2Sync.size());

        HisExam hisExam;
        for(ReqRequestInfo info : infoMap.values()) {
            hisExam = new HisExam();
            hisExam.setSheetId(info.getSheetId());
            hisExams2Sync.add(populateInfo2HisExam(info, hisExam));
        }
        hisExamRepository.saveAll(hisExams2Sync);
    }

    private HisExam populateInfo2HisExam(ReqRequestInfo info, HisExam hisExam) {
        hisExam.setPreExamDoctorId(info.getReqSheetDoctorId());
        hisExam.setPreExamDoctorName(info.getReqSheetDoctor());
        hisExam.setPreExamFromDeptId(info.getReqDepartmentId());
        hisExam.setPreExamFromDept(info.getReqDepartment());
        hisExam.setPreExamDate(info.getReqDataTime());
        hisExam.setPatientCardNum(info.getOutHospitalId());
        hisExam.setPatientName(info.getPatientName());
        hisExam.setPatientSex(info.getPatientSex());
        hisExam.setPatientBirth(info.getPatientBirth());
        hisExam.setPatientId(info.getPatientId());
        hisExam.setAccessionNum(info.getAccessionNum());
        hisExam.setExamDeptId(info.getExamKSDM());
        hisExam.setExamDept(info.getExamKSMC());
        hisExam.setPreExamFromType(info.getPatientStyle());
        hisExam.setPreExamDeviceType(info.getModalityRemark());
        hisExam.setPreExamBodyPart(info.getExamBodyPart());
        hisExam.setPreExamMethod(info.getExamMethod());

        return  hisExam;
    }

}
