package cn.gehc.cpm.domain;

import javax.persistence.*;

import lombok.Data;

import java.util.Date;

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

}
