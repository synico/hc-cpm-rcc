package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "v_request_info")
public class ReqRequestInfo {

    @Id
    @Column(name = "sheetid")
    private String sheetId;

    @Column(name = "pre_exam_doctor_id")
    private String reqSheetDoctorId;

    @Column(name = "pre_exam_doctor_name")
    private String reqSheetDoctor;

    @Column(name = "pre_exam_from_dept_id")
    private String reqDepartmentId;

    @Column(name = "pre_exam_from_dept")
    private String reqDepartment;

    @Column(name = "pre_exam_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reqDataTime;

    @Column(name = "exam_dept_id")
    private String examKSDM;

    @Column(name = "exam_dept")
    private String examKSMC;

    //0.门诊 1.急诊 2.住院 3.体检
    @Column(name = "pre_exam_from_type")
    private String patientStyle;

    @Column(name = "patient_card_num")
    private String outHospitalId;

    private String inHospitalId;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "patient_sex")
    private String patientSex;

    @Column(name = "patient_birth")
    @Temporal(TemporalType.DATE)
    private Date patientBirth;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "accession_num")
    private String accessionNum;

    private String actualExamPlace;

    @Column(name = "pre_exam_device_type")
    private String modalityRemark;

    @Column(name = "pre_exam_body_part")
    private String examBodyPart;

    @Column(name = "pre_exam_method")
    private String examMethod;

}
