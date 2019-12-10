package cn.gehc.cpm.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

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

}
