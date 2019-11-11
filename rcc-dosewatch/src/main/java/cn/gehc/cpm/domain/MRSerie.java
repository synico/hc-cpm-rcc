package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "mr_serie")
public class MRSerie implements Comparable<MRSerie> {

    @EmbeddedId
    private SerieKey serieKey;

    //aet|study_key作为全局逻辑主键
    @Column(name = "local_study_key")
    private String localStudyKey;

    //aet|id作为serie全局逻辑主键
    @Column(name = "local_serie_id")
    private String localSerieId;

    @Column(name = "series_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date seriesDate;

    @Column(name = "acquisition_datetime")
    private Date acquisitionDatetime;

    @Column(name = "acquisition_duration")
    private Double acquisitionDuration;

    @Column(name = "protocol_key")
    private Long protocolKey;

    @Column(name = "serie_id")
    private String serieId;

    @Column(name = "dtype")
    private String dType;

    @Column(name = "study_key")
    private Long studyKey;

    @Column(name = "protocol_name")
    private String protocolName;

    @Column(name = "start_slice_location")
    private Double startSliceLocation;

    @Column(name = "end_slice_location")
    private Double endSliceLocation;

    @Column(name = "series_description")
    private String seriesDescription;

    @Column(name = "dt_last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLastUpdate;

    public int compareTo(MRSerie anotherSerie) {
        int result = this.getSeriesDate().compareTo(anotherSerie.getSeriesDate());
        if(result == 0) {
            result = this.getSerieId().compareTo(anotherSerie.getSerieId());
        }
        return result;
    }

}
