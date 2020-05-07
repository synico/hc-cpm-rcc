package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 212706300
 */

@Data
@Entity
@Table(name = "nm_study")
public class NMStudy {

    @EmbeddedId
    private StudyKey studyKey;

    @Column(name = "local_study_id")
    private String localStudyId;

    @Column(name = "injection_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date injectionTime;

    @Column(name = "radioisotope_mapping_key")
    private Integer radioisotopeMappingKey;

    @Column(name = "radioisotope_name")
    private String radioisotopeName;

    @Column(name = "administered_activity")
    private Double administeredActivity;

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
        if (anObject instanceof NMStudy) {
            NMStudy anotherNMStudy = (NMStudy) anObject;
            if (this.studyKey.equals(anotherNMStudy.studyKey)) {
                return true;
            }
        }
        return false;
    }

}
