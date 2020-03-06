package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
