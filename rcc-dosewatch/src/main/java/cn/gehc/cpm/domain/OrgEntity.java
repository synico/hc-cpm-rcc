package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author 212706300
 */

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

    @Override
    public int hashCode() {
        return Objects.hash(this.orgId, this.orgName);
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof OrgEntity) {
            OrgEntity anotherOrg = (OrgEntity)anObject;
            if (this.orgId.equals(anotherOrg.orgId)
                && this.orgName.equals(anotherOrg.orgName)) {
                return true;
            }
        }
        return false;
    }

}
