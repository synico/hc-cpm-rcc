package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "v_report_reviewed")
public class RisReportReviewed {

    @Id
    @Column(name = "requisition_id")
    private String requisitionId;

    @Column(name = "sheetid")
    private String hisSheetid;

    @Column(name = "post_exam_reviewer_id")
    private String hisUserId;

    @Column(name = "post_exam_reviewer_name")
    private String userName;

    @Column(name = "approve_report_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actionDateTime;

    @Column(name = "exam_result")
    private Integer positiveFlag;

    @Column(name = "post_exam_approval_status")
    private Integer reportState;

}
