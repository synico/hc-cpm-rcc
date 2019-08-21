package cn.gehc.cpm.domain;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "his_exam")
public class HisExam {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String uid;

    //* 检查单号
    @Column(name = "sheetid")
    private String sheetId;

    //开单医师ID
    private String preExamDoctorId;

    //开单医生姓名
    private String preExamDoctorName;

    //开单科室ID
    private String preExamFromDeptId;

    //* 开单科室
    private String preExamFromDept;

    //* 开单时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preExamDate;

    //病人病历卡卡号
    private String patientCardNum;

    //病人姓名
    private String patientName;

    //病人性别
    private String patientSex;

    //病人生日
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date patientBirth;

    //病人号
    private String patientId;

    //检查号(ris有记录以后，从ris回写到his)
    private String accessionNum;

    //检查科室Id
    private String examDeptId;

    //* 检查科室
    private String examDept;

    //* 检查来源类型(0.门诊 1.急诊 2.住院 3.体检)
    private String preExamFromType;

    //* 检查是否取消
    private Boolean isCanceled = Boolean.FALSE;

    //检查取消时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date examCancelDate;

    //* 预约提交时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preExamScheduleSubmitDate;

    //* 预约时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preExamScheduleDate;

    //预约时长
    private Integer preExamScheduleDuration = 0;

    //* 检查机房
    private String examPlace;

    // 预约是否取消
    private Boolean isScheduleCanceled = Boolean.FALSE;

    // 预约取消时间
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date preExamScheduleCancelDate;

    //开单时检查设备类型
    private String preExamDeviceType;

    //开单时检查部位
    private String preExamBodyPart;

    //开单时检查方式
    private String preExamMethod;

}
