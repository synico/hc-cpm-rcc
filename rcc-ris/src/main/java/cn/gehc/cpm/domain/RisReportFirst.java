package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "v_report_first")
public class RisReportFirst {

    @Id
    @Column(name = "requisition_id")
    private String requisitionId;

    @Column(name = "sheetid")
    private String hisSheetid;

    @Column(name = "post_exam_reporter_id")
    private String hisUserId;

    @Column(name = "post_exam_reporter_name")
    private String userName;

    @Column(name = "submit_report_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDateTime;

    @Column(name = "exam_result")
    private Integer positiveFlag;

    private Integer reportState;

}
