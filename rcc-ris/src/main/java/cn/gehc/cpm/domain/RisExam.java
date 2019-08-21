package cn.gehc.cpm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "ris_exam")
public class RisExam {

    //检查流水号(join v_report_first和v_report_reviewed)
    @Id
    @Column(name = "requisition_id")
    private String requisitionId;

    //* accessionNum
    private String accessionNum;

    //* 检查单号(手工单为空)
    @Column(name = "sheetid")
    private String sheetId;

    //* 检查时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examDate;

    //* 出片时间(同examDate，冗余字段)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date postExamOutDate;

    //* patientId(同dw中patientId)
    private String patientId;

    //* 实际检查机房
    private String actualExamPlace;

    //* 检查设备类型
    private String deviceType;

    //检查技师ID
    private String technicianId;

    //检查技师名称
    private String technicianName;

    //检查部位
    private String examBodyPart;

    //* 检查方法(平扫/增强/CTA/MRA)
    private String examMethod;

    //影像StudyUID
    private String studyUid;

    //* 签到时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preRegisteDate;

    //* 报告提交时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitReportTime;

    //报告医生id
    private String postExamReporterId;

    //报告医生姓名
    private String postExamReporterName;

    //** 检查结果阴阳性(1=阳性，2=阴性)
    private String examResult;

    //* 审核报告提交时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approveReportTime;

    //报告审核医师id
    private String postExamReviewerId;

    //报告审核医师姓名
    private String postExamReviewerName;

    //报告通过状态
    private String postExamApprovalStatus;

}
