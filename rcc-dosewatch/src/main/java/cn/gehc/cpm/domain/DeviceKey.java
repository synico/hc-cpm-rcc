package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class DeviceKey implements Serializable {

    @Column(name = "ID")
    private Long id;

    @Column(name = "AET")
    private String aet;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;
        DeviceKey key = (DeviceKey) obj;
        return this.id.equals(key.id) && this.aet.equals(key.aet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.aet);
    }

}
