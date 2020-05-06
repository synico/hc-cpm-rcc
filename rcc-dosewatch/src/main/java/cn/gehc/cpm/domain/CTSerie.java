package cn.gehc.cpm.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "ct_serie")
public class CTSerie implements Comparable<CTSerie> {

    @EmbeddedId
    private SerieKey serieKey;

    /**
     * org_id|aet|device_type|study_key作为全局逻辑主键
     */
    @Column(name = "local_study_key")
    private String localStudyKey;

    /**
     * aet|id作为serie全局主键
     */
    @Column(name = "local_serie_id")
    private String localSerieId;

    @Column(name = "serie_id")
    private String serieId;

    @Column(name = "dtype")
    private String dType;

    @Column(name = "study_key")
    private Long studyKey;

    @Column(name = "aapm_factor_ssde")
    private Double aapmFactorSSDE;

    @Column(name = "ctdi_vol_ssde")
    private Double ctdiVolSSDE;

    @Column(name = "dlp_ssde")
    private Double dlpSSDE;

    @Column(name = "end_series_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endSeriesTime;

    @Column(name = "series_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date seriesDate;

    @Column(name = "acquisition_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date acquisitionDatetime;

    @Column(name = "exposure_time")
    private Float exposureTime;

    @Column(name = "target_region")
    private String targetRegion;

    @Column(name = "start_slice_location")
    private Double startSliceLocation;

    @Column(name = "end_slice_location")
    private Double endSliceLocation;

    @Column(name = "dlp")
    private Double dlp;

    @Column(name = "effective_dose")
    private Double effectiveDose;

    @Column(name = "series_description")
    private String seriesDescription;

    @Column(name = "is_repeated")
    private Boolean isRepeated = Boolean.FALSE;

    @Column(name = "protocol_key")
    private Long protocolKey;

    @Column(name = "protocol_name")
    private String protocolName;

    @Column(name = "dt_last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLastUpdate;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Override
    public int compareTo(CTSerie anotherSerie) {
        int result = this.getSeriesDate().compareTo(anotherSerie.getSeriesDate());
        if(result == 0) {
            result = this.getSerieId().compareTo(anotherSerie.getSerieId());
        }
        return result;
    }

}
