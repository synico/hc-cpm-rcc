package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "v_request_cancel_schedule_info")
public class ReqCancelScheduleInfo {

    @Id
    @Column(name = "sheetid")
    private String sheetId;

    @Column(name = "is_canceled")
    private Boolean isCanceled;

    @Column(name = "pre_exam_schedule_cancel_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date preExamScheduleCancelDate;

    private String cancelScheduleDate;

    private String cancelScheduleTime;

}
