package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author 212706300
 */

@Data
@Entity
@Table(name = "study")
public class Study implements Comparable<Study> {

    @EmbeddedId
    private StudyKey studyKey;

    /**
     * org_id|aet|modality|id作为study全局逻辑主键
     */
    @Column(name = "local_study_id")
    private String localStudyId;

    @Column(name = "ACCESSION_NUMBER")
    private String accessionNumber;

    @Column(name = "AE_KEY")
    private Integer aeKey;

    @Column(name = "DTYPE")
    private String dType;

    @Column(name = "PATIENT_ID")
    private String patientId;

    @Column(name = "PATIENT_SEX")
    private String patientSex;

    @Column(name = "PATIENT_AGE")
    private Integer patientAge;

    @Column(name = "PATIENT_NAME")
    private String patientName;

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

    @Column(name = "TARGET_REGION_COUNT")
    private Integer targetRegionCount;

    @Column(name = "PREV_LOCAL_STUDY_ID")
    private String prevLocalStudyId;

    @Column(name = "NEXT_LOCAL_STUDY_ID")
    private String nextLocalStudyId;

    /**
     * 1 = published, 2 = marked for deletion
     */
    @Column(name = "PUBLISHED")
    private Integer published = StudyStatus.PUBLISHED.getStatusId();

    @Column(name = "SD_KEY")
    private Integer studyDescKey;

    @Column(name = "STUDY_DESCRIPTION")
    private String studyDescription;

    @Column(name = "PERFORMING_PHYSICIAN")
    private String performingPhysician;

    @Column(name = "HAS_REPEATED_SERIES")
    private Boolean hasRepeatedSeries = Boolean.FALSE;

    @Column(name = "DT_LAST_UPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLastUpdate;

    @Column(name = "CREATE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Override
    public int compareTo(Study anotherStudy) {
        int result = 0;
        if (this.getStudyDate() == null || anotherStudy.getStudyDate() == null) {
            //do nothing
        } else {
            result = this.getStudyDate().compareTo(anotherStudy.getStudyDate());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return this.getStudyKey().hashCode();
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Study) {
            Study anotherStudy = (Study)anObject;
            if (this.studyKey.equals(anotherStudy.studyKey)) {
                return true;
            }
        }
        return false;
    }

    public enum StudyStatus {
        PUBLISHED(1),
        MARK_FOR_DELETION(2);

        private Integer statusId;

        StudyStatus(Integer statusId) {
            this.statusId = statusId;
        }

        public Integer getStatusId() {
            return statusId;
        }
    }

}
