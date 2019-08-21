package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "device")
public class Device {

    @EmbeddedId
    private DeviceKey deviceKey;

    @Column(name = "AE_NAME", insertable = false, updatable = false)
    private String aeName;

    @Column(name = "DEVICE_TYPE")
    private String deviceType;

    @Column(name = "DEVICE_MODEL")
    private String deviceModel;

    @Column(name = "LOCATION", insertable = false, updatable = false)
    private String location;

    @Column(name = "MF_CODE")
    private String mfCode;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATION_NAME")
    private String stationName;

    @Column(name = "TIMEZONE")
    private String timezone;

    @Column(name = "DT_LAST_UPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLastUpdate;

    @Column(name = "ORG_NAME", insertable = false, updatable = false)
    private String orgName;

    @Column(name = "PHOTO_LINK", insertable = false, updatable = false)
    private String photoLink;

    @Column(name = "PREPARE_TIME1", insertable = false, updatable = false)
    private Float prepareTime1;

    @Column(name = "PREPARE_TIME2", insertable = false, updatable = false)
    private Float prepareTime2;

    @Column(name = "SITE", insertable = false, updatable = false)
    private String site;

}
