package cn.gehc.ii.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class DeviceScheduleKey implements Serializable {

    @Column(name = "device_key")
    private String deviceKey;

    @Column(name = "start_date")
    private String startDate;

    public void of(String deviceKey, String startDate) {
        this.deviceKey = deviceKey;
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DeviceScheduleKey) {
            DeviceScheduleKey dsKey = (DeviceScheduleKey)obj;
            if(this.deviceKey.equals(dsKey.getDeviceKey())
                && this.startDate.equals(dsKey.getStartDate())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.deviceKey, this.startDate);
    }

    @Override
    public String toString() {
        return this.deviceKey + "|" + this.startDate;
    }

}
