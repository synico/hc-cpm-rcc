package cn.gehc.ii.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class NisExamKey implements Serializable {

    @Column(name = "sheetid")
    private String sheetId;

    @Column(name = "patient_id")
    private String patientId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NisExamKey) {
            NisExamKey anotherNisExamKey = (NisExamKey)obj;
            return this.sheetId.equals(anotherNisExamKey.getSheetId())
                    && this.patientId.equals(anotherNisExamKey.getPatientId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sheetId, this.patientId);
    }

    public String toString() {
        return this.sheetId + "|" + this.patientId;
    }
}
