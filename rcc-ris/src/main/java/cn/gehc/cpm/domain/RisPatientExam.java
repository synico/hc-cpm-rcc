package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "v_patient_exam")
public class RisPatientExam {

    @Id
    @Column(name = "requisition_id")
    private String requisitionId;

    @Column(name = "sheetid")
    private String hisSheetid;

    @Column(name = "exam_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date examDateTime;

    @Column(name = "post_exam_out_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date postExamOutDate;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "accession_num")
    private String accessionNumber;

    @Column(name = "actual_exam_place")
    private String roomName;

    @Column(name = "device_type")
    private String modalityRemark;

    @Column(name = "technician_id")
    private String techId;

    @Column(name = "technician_name")
    private String techUserName;

    @Column(name = "exam_body_part")
    private String examBodyPart;

    @Column(name = "exam_method")
    private String examMethod;

    @Column(name = "study_uid")
    private String imageInstanceUID;


}
