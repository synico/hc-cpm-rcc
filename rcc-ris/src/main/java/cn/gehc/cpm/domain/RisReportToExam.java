package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "v_report_to_exam")
public class RisReportToExam {

    @Id
    @Column(name = "requisition_id")
    private String requisitionId;

    @Column(name = "sheetid")
    private String sheetId;

    @Column(name = "pre_registe_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportToDateTime;

    @Column(name = "patientId")
    private String patientId;

    @Column(name = "accession_num")
    private String accessionNumber;

}
