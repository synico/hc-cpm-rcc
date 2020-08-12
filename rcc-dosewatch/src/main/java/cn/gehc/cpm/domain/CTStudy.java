package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 212706300
 */

@Data
@Entity
@Table(name = "ct_study")
public class CTStudy {

    @EmbeddedId
    private StudyKey studyKey;

    /**
     * aet|id作为study全局逻辑主键
     */
    @Column(name = "local_study_id")
    private String localStudyId;

    @Column(name = "START_OF_XRAY_IRRADIATION")
    private Date startOfXrayIrradiation;

    @Column(name = "END_OF_XRAY_IRRADIATION")
    private Date endOfXrayIrradiation;

    @Column(name = "CT_DOSE_LENGTH_PRODUCT_TOTAL")
    private Double dlpTotal;

    @Column(name = "DLP_SSDE")
    private Double dlpSSDE;

    @Column(name = "EXAM_CTDI")
    private Double examCTDI;

    @Column(name = "NUM_SERIES")
    private Integer numSeries;

    @Column(name = "PROTOCOL_KEY")
    private Long protocolKey;

    @Column(name = "PROTOCOL_NAME")
    private String protocolName;

    @Column(name = "DT_LAST_UPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLastUpdate;

    @Column(name = "CREATE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Override
    public int hashCode() {
        return this.studyKey.hashCode();
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof CTStudy) {
            CTStudy anotherCTStudy = (CTStudy) anObject;
            if (this.studyKey.equals(anotherCTStudy.studyKey)) {
                return true;
            }
        }
        return false;
    }

}
