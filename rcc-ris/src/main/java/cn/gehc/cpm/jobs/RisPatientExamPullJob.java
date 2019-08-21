/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.RisExam;
import cn.gehc.cpm.domain.RisPatientExam;
import cn.gehc.cpm.repository.RisExamRepository;
import cn.gehc.cpm.repository.RisPatientExamRepository;
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
public class RisPatientExamPullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(RisPatientExamPullJob.class);

    @Autowired
    private RisPatientExamRepository patientExamRepository;

    @Autowired
    private RisExamRepository risExamRepository;

    public void insertBatchExam(@Headers Map<String, Object> headers, @Body Object body) {

        List<Map<String, Object>> examMap = (List<Map<String, Object>>) body;

        Date maxDate = saveData2DB(examMap);

        //记录读取进度
        if (maxDate != null) {
            this.updateLastPullValue(headers, DateTimeUtil.getStrDate(maxDate, "yyyyMMddHHmmss"));
        }
    }

    private Date saveData2DB(List<Map<String, Object>> data) {
        List<RisPatientExam> infos = new ArrayList<>(100);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        RisPatientExam info;
        Date maxDate = null;
        StringBuilder warningMsg;
        for(Map<String, Object> obj : data) {
            warningMsg = new StringBuilder("Empty field: ");
            info = new RisPatientExam();

            if(DataConvertUtils.isBlankField(obj.get("RequisitionID"))) {
                warningMsg.append("RequisitionID,");
            } else {
                info.setRequisitionId(String.valueOf(obj.get("RequisitionID")));
            }

            if(DataConvertUtils.isBlankField(obj.get("HISsheetid"))) {
                warningMsg.append("HISsheetid,");
            } else {
                info.setHisSheetid(String.valueOf(obj.get("HISsheetid")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ExamDateTime"))) {
                warningMsg.append("ExamDateTime,");
            } else {
                try {
                    info.setExamDateTime(
                            dateFormat.parse(String.valueOf(obj.get("ExamDateTime")))
                    );
                    info.setPostExamOutDate(
                            dateFormat.parse(String.valueOf(obj.get("ExamDateTime")))
                    );

                    // get max date
                    if(maxDate == null) {
                        maxDate = info.getExamDateTime();
                    } else {
                        maxDate = (maxDate.compareTo(info.getExamDateTime()) > 0) ? maxDate : info.getExamDateTime();
                    }
                } catch (Exception ex) {
                    log.warn("RequisitionId: " + info.getRequisitionId() + ", parse examDateTime failed.");
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

            info.setRoomName(String.valueOf(obj.get("RoomName")));

            if(DataConvertUtils.isBlankField(obj.get("ModalityRemark"))) {
                warningMsg.append("ModalityRemark,");
            } else {
                info.setModalityRemark(String.valueOf(obj.get("ModalityRemark")));
            }

            if(DataConvertUtils.isBlankField(obj.get("TechID"))) {
                warningMsg.append("TechID,");
            } else {
                String techId = String.valueOf(obj.get("TechID"));
                String[] techIds = techId.split(",");
                if(techIds.length == 2 && techIds[0].equals(techIds[1])) {
                    info.setTechId(techIds[0]);
                } else {
                    info.setTechId(techId);
                }
            }

            if(DataConvertUtils.isBlankField(obj.get("TechUserName"))) {
                warningMsg.append("TechUserName,");
            } else {
                String techUserName = String.valueOf(obj.get("TechUserName"));
                String[] techUserNames = techUserName.split(",");
                if(techUserNames.length == 2 && techUserNames[0].equals(techUserNames[1])) {
                    info.setTechUserName(techUserNames[0]);
                } else {
                    info.setTechUserName(techUserName);
                }
            }

            if(DataConvertUtils.isBlankField(obj.get("ExamBodyPart"))) {
                warningMsg.append("ExamBodyPart,");
            } else {
                info.setExamBodyPart(String.valueOf(obj.get("ExamBodyPart")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ExamMethod"))) {
                warningMsg.append("ExamMethod,");
            } else {
                info.setExamMethod(String.valueOf(obj.get("ExamMethod")));
            }

            if(DataConvertUtils.isBlankField(obj.get("ImageInstanceUID"))) {
                warningMsg.append("ImageInstanceUID,");
            } else {
                info.setImageInstanceUID(String.valueOf(obj.get("ImageInstanceUID")));
            }

            infos.add(info);

            if(log.isWarnEnabled() && warningMsg.toString().endsWith(",")) {
                log.warn(warningMsg.toString());
            }
        }

        Iterable<RisPatientExam> result = patientExamRepository.saveAll(infos);

        saveData2RisExam(infos);

        return maxDate;
    }

    private void saveData2RisExam(List<RisPatientExam> infos) {
        Map<String, RisPatientExam> infoMap = new HashMap<>(100);
        List<String> requisitionIds = new ArrayList<>(100);
        List<RisExam> risExams2Sync = new ArrayList<>(100);

        infos.forEach(info -> {
            requisitionIds.add(info.getRequisitionId());
            infoMap.put(info.getRequisitionId().trim(), info);
        });

        List<RisExam> risExamList;
        if(requisitionIds.size() > 0) {
            risExamList = risExamRepository.findByRequisitionIdIn(requisitionIds);
            risExamList.forEach(risExam -> {
                RisPatientExam info = infoMap.remove(risExam.getRequisitionId());
                risExams2Sync.add(populateInfo2RisExam(info, risExam));
            });
            log.info("{} records will be updated to ris_exam", risExams2Sync.size());
        }

        RisExam risExam;
        for(RisPatientExam info : infoMap.values()) {
            if(info.getRequisitionId() != null) {
                risExam = new RisExam();
                risExam.setRequisitionId(info.getRequisitionId());
                risExams2Sync.add(populateInfo2RisExam(info, risExam));
            } else {
                //do nothing
            }
        }
        risExamRepository.saveAll(risExams2Sync);
    }

    private RisExam populateInfo2RisExam(RisPatientExam info, RisExam risExam) {
        risExam.setSheetId(info.getHisSheetid());
        risExam.setExamDate(info.getExamDateTime());
        risExam.setPatientId(info.getPatientId());
        risExam.setAccessionNum(info.getAccessionNumber());
        risExam.setDeviceType(info.getModalityRemark());
        risExam.setTechnicianId(info.getTechId());
        risExam.setTechnicianName(info.getTechUserName());
        risExam.setExamBodyPart(info.getExamBodyPart());
        risExam.setExamMethod(info.getExamMethod());
        risExam.setStudyUid(info.getImageInstanceUID());
        risExam.setPostExamOutDate(info.getExamDateTime());
        risExam.setActualExamPlace(info.getRoomName());

        return risExam;
    }

}
