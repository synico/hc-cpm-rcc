package cn.gehc.ii.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "nis_exam")
public class NisExam {

    @EmbeddedId
    private NisExamKey nisExamKey;

    /**
     * 检查流水号，唯一
     */
    @Column(name = "requisition_id")
    private String requisitionId;

//    /**
//     * 检查单号，唯一，某些情况下可能为空
//     */
//    @Column(name = "sheetid")
//    private String sheetId;

    /**
     * 开单医师ID
     */
    @Column(name = "pre_exam_doctor_id")
    private String preExamDoctorId;

    /**
     * 开单医师姓名
     */
    @Column(name = "pre_exam_doctor_name")
    private String preExamDoctorName;

    /**
     * 开单科室ID
     */
    @Column(name = "pre_exam_from_dept_id")
    private String preExamFromDeptId;

    /**
     * 开单科室名称
     */
    @Column(name = "pre_exam_from_dept")
    private String preExamFromDept;

    /**
     * 开单时间 (格式 : '年月日 时分秒')
     */
    @Column(name = "pre_exam_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preExamDate;

    /**
     * 患者病历卡卡号
     */
    @Column(name = "patient_card_num")
    private String patientCardNum;

//    /**
//     * 患者编号
//     */
//    @Column(name = "patient_id")
//    private String patientId;

    /**
     * 患者姓名
     */
    @Column(name = "patient_name")
    private String patientName;

    /**
     * 患者性别
     */
    @Column(name = "patient_sex")
    private String patientSex;

    /**
     * 患者出生年月 (格式 : '年月日 时分秒')
     */
    @Column(name = "patient_birth")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date patientBirth;

    /**
     * 检查科室ID
     */
    @Column(name = "exam_dept_id")
    private String examDeptId;

    /**
     * 检查科室名称
     */
    @Column(name = "exam_dept")
    private String examDept;

    /**
     * 患者来源 (0.门诊 1.急诊 2.住院 3.体检)
     */
    @Column(name = "pre_exam_from_type")
    private String preExamFromType;

    /**
     * 检查是否被取消
     */
    @Column(name = "is_canceled")
    private Boolean isCanceled = Boolean.FALSE;

    /**
     * 检查取消时间
     */
    @Column(name = "exam_cancel_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examCancelDate;

    /**
     * 预约提交时间 (格式 : '年月日 时分秒')
     */
    @Column(name = "pre_exam_schedule_submit_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preExamScheduleSubmitDate;

    /**
     * 预约检查时间 (格式 : '年月日 时分秒')
     */
    @Column(name = "pre_exam_schedule_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preExamScheduleDate;

    /**
     * 预约检查时长 (单位 : 分钟)
     */
    @Column(name = "pre_exam_schedule_duration")
    private Integer preExamScheduleDuration = 0;

    /**
     * 预约检查机房
     */
    @Column(name = "exam_place")
    private String examPlace;

    /**
     * 预约是否被取消
     */
    @Column(name = "is_schedule_canceled")
    private Boolean isScheduleCanceled = Boolean.FALSE;

    /**
     * 预约取消时间 (格式 : '年月日 时分秒')
     */
    @Column(name = "pre_exam_schedule_cancel_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preExamScheduleCancelDate;

    /**
     * 开单时检查设备类型
     */
    @Column(name = "pre_exam_device_type")
    private String preExamDeviceType;

    /**
     * 开单时检查部位
     */
    @Column(name = "pre_exam_body_part")
    private String preExamBodyPart;

    /**
     * 开单时检查方式
     */
    @Column(name = "pre_exam_method")
    private String preExamMethod;

    /**
     * 检查检索号
     */
    @Column(name = "accession_number")
    private String accessionNumber;

    /**
     * 检查时检查设备类型
     */
    @Column(name = "device_type")
    private String deviceType;

    /**
     * 实际检查机房 (机房名称或位置)
     */
    @Column(name = "actual_exam_place")
    private String actualExamPlace;

    /**
     * 实际检查方式
     */
    @Column(name = "exam_method")
    private String examMethod;

    /**
     * 实际检查部位
     */
    @Column(name = "exam_body_part")
    private String examBodyPart;

    /**
     * 影像study uid
     */
    @Column(name = "study_uid")
    private String studyUid;

    /**
     * 患者检查签到时间 (格式 : '年月日 时分秒')
     */
    @Column(name = "pre_registe_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preRegisteDate;

    /**
     * 时间检查时间或检查开始时间 (格式 : '年月日 时分秒')
     */
    @Column(name = "exam_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examDate;

    /**
     * 检查技师ID
     */
    @Column(name = "technician_id")
    private String technicianId;

    /**
     * 检查技师姓名
     */
    @Column(name = "technician_name")
    private String technicianName;

    /**
     * 出片时间 (格式 : '年月日 时分秒')
     */
    @Column(name = "post_exam_out_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date postExamOutDate;

    /**
     * 报告提交时间 (格式 : '年月日 时分秒')
     */
    @Column(name = "submit_report_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitReportTime;

    /**
     * 书写报告医师ID
     */
    @Column(name = "post_exam_reporter_id")
    private String postExamReporterId;

    /**
     * 书写报告医师姓名
     */
    @Column(name = "post_exam_reporter_name")
    private String postExamReporterName;

    /**
     * 报告审核提交时间 (格式 : '年月日 时分秒')
     */
    @Column(name = "approve_report_time")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approveReportTime;

    /**
     * 审核报告医师ID
     */
    @Column(name = "post_exam_reviewer_id")
    private String postExamReviewerId;

    /**
     * 审核报告医师姓名
     */
    @Column(name = "post_exam_reviewer_name")
    private String postExamReviewerName;

    /**
     * 检查结果阴阳性 (1=阳性,2=阴性)
     */
    @Column(name = "exam_result")
    private String examResult;

    /**
     * 报告通过状态
     */
    @Column(name = "post_exam_approval_status")
    private String postExamApprovalStatus;

    /**
     * 数据来源，internal use only
     */
    @Column(name = "source_system")
    private String sourceSystem;

    @Column(name = "last_update_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastUpdateDate;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj instanceof NisExam) {
            NisExam anotherNisExam = (NisExam)obj;
            return this.getNisExamKey().equals(anotherNisExam.getNisExamKey());
        }
        return false;
    }

}
