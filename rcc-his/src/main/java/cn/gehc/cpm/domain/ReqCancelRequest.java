package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "v_cancel_request")
public class ReqCancelRequest {

    @Id
    @Column(name = "sheetit")
    private String sheetId;

    @Column(name = "is_canceled")
    private Boolean isCanceled;

    private String cancelExamDoctor;

    @Column(name = "exam_cancel_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date asCancelExamDateTime;

}
