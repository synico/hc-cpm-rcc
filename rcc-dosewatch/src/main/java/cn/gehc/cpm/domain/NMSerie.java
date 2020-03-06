package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "nm_serie")
public class NMSerie {

    @EmbeddedId
    private SerieKey serieKey;

    //org_id|aet|device_type|study_key作为全局逻辑主键
    @Column(name = "local_study_key")
    private String localStudyKey;

    @Column(name = "local_serie_id")
    private String localSerieId;

    @Column(name = "serie_id")
    private String serieId;

    @Column(name = "dtype")
    private String dType;

    @Column(name = "series_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date seriesDate;

    @Column(name = "target_region")
    private String targetRegion;

    @Column(name = "series_description")
    private String seriesDescription;

    @Column(name = "protocol_key")
    private String protocolKey;

    @Column(name = "protocol_name")
    private String protocalName;

    @Column(name = "dt_last_update")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLastUpdate;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

}
