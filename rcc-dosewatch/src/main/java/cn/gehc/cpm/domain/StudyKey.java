package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class StudyKey implements Serializable {

    @Column(name = "ID")
    private Long id;

    @Column(name = "AET")
    private String aet;

    @Column(name = "MODALITY")
    private String modality;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;
        StudyKey key = (StudyKey) obj;
        return this.id.equals(key.id) && this.aet.equals(key.aet) && this.modality.equals(key.modality);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.aet, this.modality, this.id);
    }

    @Override
    public String toString() {
        return this.aet + "|" + this.modality + "|" + this.id;
    }

}
