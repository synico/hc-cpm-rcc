package cn.gehc.cpm.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

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

}
