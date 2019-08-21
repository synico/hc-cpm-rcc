/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 *
 * @author 212579464
 */
@Data
@Entity
public class TimerJob {

    public enum JobStatus {
        Starting,Running, End,Pending
    }
    
    public enum JobType {
        ReadDoseDB
    }
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    
    private String jobName;

    private String jobType;
    
    private String status;
    
    private String lastPolledValue;
    
    private Date lastStartTime;

    private Date lastUpdatedTime;
}
