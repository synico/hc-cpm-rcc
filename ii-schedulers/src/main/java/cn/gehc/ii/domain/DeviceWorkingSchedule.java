package cn.gehc.ii.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "device_working_schedule")
public class DeviceWorkingSchedule {

    @EmbeddedId
    private DeviceScheduleKey deviceScheduleKey;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "opening_hours")
    private String openingHours;

    @Column(name = "closing_hours")
    private String closingHours;

    @CreatedDate
    @Column(name = "created_time")
    private Date createdTime;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @LastModifiedDate
    @Column(name = "last_modified_time")
    private Date lastModifiedTime;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

}
