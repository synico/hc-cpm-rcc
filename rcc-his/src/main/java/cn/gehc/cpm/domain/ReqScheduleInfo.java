package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "v_request_schedule_info")
public class ReqScheduleInfo {

    @Id
    @Column(name = "sheetid")
    private String sheetId;

    @Column(name = "pre_exam_schedule_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduleDate;

    @Column(name = "pre_exam_schedule_submit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduleOperateDateTime;

    @Column(name = "pre_exam_schedule_duration")
    private Integer scheduleDuration;

    private String scheduleTime;

    @Column(name = "exam_place")
    private String roomName;

}
