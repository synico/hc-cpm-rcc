package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class SerieKey implements Serializable {

    @Column(name = "ORG_ID")
    private Long orgId;

    @Column(name = "AET")
    private String aet;

    @Column(name = "MODALITY")
    private String modality;

    //id in dw serie table
    @Column(name = "ID")
    private Long id;

    //org_id|aet|modality
    @Column(name = "device_key")
    private String deviceKey;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;
        SerieKey key = (SerieKey) obj;
        return this.orgId.equals(key.orgId)
                && this.aet.equals(key.aet)
                && this.modality.equals(key.modality)
                && this.id.equals(key.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.orgId, this.aet, this.modality, this.id);
    }

    @Override
    public String toString() {
        return this.orgId + "|" + this.aet + "|" + this.modality + "|" + this.id;
    }

}
