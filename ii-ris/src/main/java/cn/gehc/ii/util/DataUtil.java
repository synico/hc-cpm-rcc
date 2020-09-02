package cn.gehc.ii.util;

import cn.gehc.ii.domain.NisExam;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataUtil {

    private static final Logger log = LoggerFactory.getLogger(DataUtil.class);

    public enum DatabaseType {
        MYSQL,
        MSSQL,
        POSTGRES
    }

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final String LOCAL_TIME_ZONE = "Asia/Shanghai";

    public static final ZoneId localZoneId = ZoneId.of(LOCAL_TIME_ZONE);

    public static <T> T getValueFromProperties(Map<String, Object> props, String key, Class<T> type) {
        T result = null;
        Object value = props.get(key);
        if (value != null) {
            log.debug("value type: {}, type name: {}", type.toString(), type.getName());
            LocalDateTime localDateTime;
            switch (type.getName()) {
                case "java.lang.String" :
                    result = (T) value.toString();
                    break;
                case "java.lang.Long" :
                    result = (T) Long.valueOf(value.toString());
                    break;
                case "java.lang.Integer" :
                    result = (T) Integer.valueOf(value.toString());
                    break;
                case "java.lang.Double" :
                    result = (T) Double.valueOf(value.toString());
                    break;
                case "java.lang.Float" :
                    result = (T) Float.valueOf(value.toString());
                    break;
                case "java.lang.Boolean" :
                    result = (T) Boolean.valueOf(value.toString());
                    break;
                case "java.util.Date" :
                    localDateTime = LocalDateTime.parse(value.toString(), dateFormatter);
                    result = (T) new java.util.Date(localDateTime.atZone(localZoneId).toInstant().toEpochMilli());
                    break;
                case "java.sql.Date" :
                    localDateTime = LocalDateTime.parse(value.toString(), dateFormatter);
                    result = (T) new java.sql.Date(localDateTime.atZone(localZoneId).toInstant().toEpochMilli());
                    break;
            }
        }
        log.debug("result: {}", result);
        return result;
    }

    public static NisExam convertRow2Exam(Map<String, Object> row) {
        NisExam exam = new NisExam();
        exam.setRequisitionId(getValueFromProperties(row, "requisition_id", String.class));
        exam.setSheetId(getValueFromProperties(row, "sheetid", String.class));
        exam.setPreExamDoctorId(getValueFromProperties(row, "pre_exam_doctor_id", String.class));
        exam.setPreExamDoctorName(getValueFromProperties(row, "pre_exam_doctor_name", String.class));
        exam.setPreExamFromDeptId(getValueFromProperties(row, "pre_exam_from_dept_id", String.class));
        exam.setPreExamFromDept(getValueFromProperties(row, "pre_exam_from_dept", String.class));
        exam.setPreExamDate(getValueFromProperties(row, "pre_exam_date", java.util.Date.class));
        exam.setPatientCardNum(getValueFromProperties(row, "patient_card_num", String.class));
        exam.setPatientId(getValueFromProperties(row, "patient_id", String.class));
        exam.setPatientName(getValueFromProperties(row, "patient_name", String.class));
        exam.setPatientSex(getValueFromProperties(row, "patient_sex", String.class));
        exam.setPatientBirth(getValueFromProperties(row, "patient_birth", java.util.Date.class));
        exam.setExamDeptId(getValueFromProperties(row, "exam_dept_id", String.class));
        exam.setExamDept(getValueFromProperties(row, "exam_dept", String.class));
        exam.setPreExamFromType(getValueFromProperties(row, "pre_exam_from_type", String.class));
        exam.setIsCanceled(getValueFromProperties(row, "is_canceled", Boolean.class));
        exam.setExamCancelDate(getValueFromProperties(row, "exam_cancel_date", java.util.Date.class));
        exam.setPreExamScheduleSubmitDate(getValueFromProperties(row, "pre_exam_schedule_submit_date", java.util.Date.class));
        exam.setPreExamScheduleDate(getValueFromProperties(row, "pre_exam_schedule_date", java.util.Date.class));
        exam.setPreExamScheduleDuration(getValueFromProperties(row, "pre_exam_schedule_duration", Integer.class));
        exam.setExamPlace(getValueFromProperties(row, "exam_place", String.class));
        exam.setIsScheduleCanceled(getValueFromProperties(row, "is_schedule_canceled", Boolean.class));
        exam.setPreExamScheduleCancelDate(getValueFromProperties(row, "pre_exam_schedule_cancel_date", java.util.Date.class));
        exam.setPreExamDeviceType(getValueFromProperties(row, "pre_exam_device_type", String.class));
        exam.setPreExamBodyPart(getValueFromProperties(row, "pre_exam_body_part", String.class));
        exam.setPreExamMethod(getValueFromProperties(row, "pre_exam_method", String.class));
        exam.setAccessionNumber(getValueFromProperties(row, "accession_number", String.class));
        exam.setDeviceType(getValueFromProperties(row, "device_type", String.class));
        exam.setActualExamPlace(getValueFromProperties(row, "actual_exam_place", String.class));
        exam.setExamMethod(getValueFromProperties(row, "exam_method", String.class));
        exam.setExamBodyPart(getValueFromProperties(row, "exam_body_part", String.class));
        exam.setStudyUid(getValueFromProperties(row, "study_uid", String.class));
        exam.setPreRegisteDate(getValueFromProperties(row, "pre_registe_date", java.util.Date.class));
        exam.setExamDate(getValueFromProperties(row, "exam_date", java.util.Date.class));
        exam.setTechnicianId(getValueFromProperties(row, "technician_id", String.class));
        exam.setTechnicianName(getValueFromProperties(row, "technician_name", String.class));
        exam.setPostExamOutDate(getValueFromProperties(row, "post_exam_out_date", java.util.Date.class));
        exam.setSubmitReportTime(getValueFromProperties(row, "submit_report_time", java.util.Date.class));
        exam.setPostExamReporterId(getValueFromProperties(row, "post_exam_reporter_id", String.class));
        exam.setPostExamReporterName(getValueFromProperties(row, "post_exam_reporter_name", String.class));
        exam.setApproveReportTime(getValueFromProperties(row, "approve_report_time", java.util.Date.class));
        exam.setPostExamReviewerId(getValueFromProperties(row, "post_exam_reviewer_id", String.class));
        exam.setPostExamReviewerName(getValueFromProperties(row, "post_exam_reviewer_name", String.class));
        exam.setExamResult(getValueFromProperties(row, "exam_result", String.class));
        exam.setPostExamApprovalStatus(getValueFromProperties(row, "post_exam_approval_status", String.class));
        exam.setSourceSystem(getValueFromProperties(row, "source_system", String.class));
        exam.setLastUpdateDate(getValueFromProperties(row, "last_update_date", java.util.Date.class));

        return exam;
    }

    public static String convertDate2String(java.util.Date date, String format) {
        return dateFormatter.format(LocalDateTime.ofInstant(date.toInstant(), localZoneId));
    }

}
