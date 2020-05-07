package cn.gehc.cpm.domain;

import javax.persistence.*;

import lombok.Data;

import java.util.Date;

/**
 * @author 212706300
 */

@Data
@Entity
@Table(name = "mr_study")
public class MRStudy {

    @EmbeddedId
    private StudyKey studyKey;

    @Column(name = "local_study_id")
    private String localStudyId;

    @Column(name = "protocol_key")
    private Long protocolKey;

    @Column(name = "protocol_name")
    private String protocolName;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Override
    public int hashCode() {
        return this.studyKey.hashCode();
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof MRStudy) {
            MRStudy anotherMRStudy = (MRStudy) anObject;
            if (this.studyKey.equals(anotherMRStudy.studyKey)) {
                return true;
            }
        }
        return false;
    }

}
