package cn.gehc.cpm.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

/**
 * @author 212706300
 */

@Data
@Entity
@Table(name = "xa_serie")
public class XASerie implements Comparable<XASerie> {

    @EmbeddedId
    private SerieKey serieKey;

    /**
     * aet|study_key作为全局逻辑主键
     */
    @Column(name = "local_study_key")
    private String localStudyKey;

    /**
     * aet|id作为serie全局逻辑主键
     */
    @Column(name = "local_serie_id")
    private String localSerieId;

    @Column(name = "series_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date seriesDate;

    @Column(name = "exposure_time")
    private Float exposureTime;

    @Column(name = "protocol_key")
    private Long protocolKey;

    @Column(name = "protocol_name")
    private String protocolName;

    @Column(name = "serie_id")
    private String serieId;

    @Column(name = "dtype")
    private String dType;

    @Column(name = "study_key")
    private Long studyKey;

    @Column(name = "target_region")
    private String targetRegion;

    @Column(name = "series_description")
    private String seriesDescription;

    @Column(name = "serie_type")
    private String serieType;

    @Column(name = "series_sub_type")
    private String seriesSubType;

    @Column(name = "serie_dap")
    private Double serieDap;

    @Column(name = "effective_dose")
    private Double effectiveDose;

    @Column(name = "dt_last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLastUpdate;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Override
    public int hashCode() {
        return this.getSerieKey().hashCode();
    }

    @Override
    public boolean equals(Object anObject) {
        if(this == anObject) {
            return true;
        }
        if(anObject instanceof XASerie) {
            XASerie anotherSerie = (XASerie)anObject;
            if(this.getSerieKey().equals(anotherSerie.getSerieKey())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(XASerie anotherSerie) {
        int result = this.getSeriesDate().compareTo(anotherSerie.getSeriesDate());
        if(result == 0) {
          result = this.getSerieId().compareTo(anotherSerie.getSerieId());
        }
        return result;
    }

}
