package cn.gehc.cpm.domain;

import javax.persistence.*;

import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "xa_study")
public class XAStudy {

  @EmbeddedId
  private StudyKey studyKey;

  //aet|id作为study全局逻辑主键
  @Column(name = "local_study_id")
  private String localStudyId;

  @Column(name = "dap")
  private Double dap;

  @Column(name = "fluoro_dap")
  private Double fluoroDap;

  @Column(name = "record_dap")
  private Double recordDap;

  @Column(name = "protocol_key")
  private Long protocolKey;

  @Column(name = "protocol_name")
  private String protocolName;

  @Column(name = "create_time")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTime;

}
