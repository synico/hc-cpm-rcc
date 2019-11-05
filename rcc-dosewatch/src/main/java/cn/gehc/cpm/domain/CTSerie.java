package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

@Data
@Entity
@Table(name = "ct_serie")
public class CTSerie implements Comparable<CTSerie> {

    @EmbeddedId
    private SerieKey serieKey;

    //aet|study_key作为全局逻辑主键
    @Column(name = "local_study_key")
    private String localStudyKey;

    //aet|id作为serie全局主键
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

    @Column(name = "dt_last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLastUpdate;

    public int compareTo(CTSerie anotherSerie) {
        int result = this.getSeriesDate().compareTo(anotherSerie.getSeriesDate());
        if(result == 0) {
            result = this.getSerieId().compareTo(anotherSerie.getSerieId());
        }
        return result;
    }

}
