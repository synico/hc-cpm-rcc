package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Consider the installation processes of dosewatch, AET and DEVICE_TYPE will guarantee the ae is unique
 * in single hospital. With additional field ORG_ID will guarantee the ae is unique in a hospital union which
 * is composed of more than one hospital.
 */

@Data
@Embeddable
public class DeviceKey implements Serializable {

    @Column(name = "ORG_ID")
    private Long orgId;

    @Column(name = "AET")
    private String aet;

    @Column(name = "DEVICE_TYPE")
    private String deviceType;

    public void of(Long orgId, String aet, String deviceType) {
        this.orgId = orgId;
        this.aet = aet;
        this.deviceType = deviceType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj instanceof DeviceKey) {
            DeviceKey anotherDeviceKey = (DeviceKey)obj;
            if(this.orgId.equals(anotherDeviceKey.orgId)
                && this.aet.equals(anotherDeviceKey.aet)
                && this.deviceType.equals(anotherDeviceKey.deviceType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.orgId, this.aet, this.deviceType);
    }

    @Override
    public String toString() {
        return this.orgId + "|" + this.aet + "|" + this.deviceType;
    }

}
