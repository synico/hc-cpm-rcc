package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "org_entity")
public class OrgEntity {

    @Id
    @Column(name = "org_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orgId;

    @Column(name = "org_name")
    private String orgName;

    @Column(name = "org_level")
    private Integer orgLevel = 2;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

}
