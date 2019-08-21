package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "study")
public class Study {

    @EmbeddedId
    private StudyKey studyKey;

    //aet|id作为study全局逻辑主键
    @Column(name = "local_study_id")
    private String localStudyId;

    @Column(name = "ACCESSION_NUMBER")
    private String accessionNumber;

    @Column(name = "AE_KEY")
    private Integer aeKey;

    @Column(name = "DTYPE")
    private String dType;

    @Column(name = "MODALITY")
    private String modality;

    @Column(name = "PATIENT_KEY")
    private Long patientKey;

    @Column(name = "PATIENT_AGE")
    private Integer patientAge;

    @Column(name = "STUDY_ID")
    private String studyId;

    @Column(name = "STUDY_INSTANCE_UID")
    private String studyInstanceUid;

    @Column(name = "STUDY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date studyDate;

    @Column(name = "STUDY_START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date studyStartTime;

    @Column(name = "STUDY_END_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date studyEndTime;

    @Column(name = "DT_LAST_UPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLastUpdate;

}
