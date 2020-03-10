package cn.gehc.cpm.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Table(name = "device")
public class Device {

    @EmbeddedId
    private DeviceKey deviceKey;

    @Column(name = "id")
    private Long id;

    @Column(name = "AE_NAME", insertable = false, updatable = false)
    private String aeName;

    @Column(name = "DEVICE_MODEL")
    private String deviceModel;

    @Column(name = "LOCATION", insertable = false, updatable = false)
    private String location;

    @Column(name = "DASHBOARD_PAGE", insertable = false, updatable = false)
    private String dashboardPage;

    @Column(name = "MF_CODE")
    private String mfCode;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATION_NAME")
    private String stationName;

    @Column(name = "TIMEZONE")
    private String timezone;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = Boolean.TRUE;

    @Column(name = "DT_LAST_UPDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtLastUpdate;

    @Column(name = "CREATE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

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

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = Boolean.FALSE;
        if(obj instanceof Device) {
            if(this.getDeviceKey().equals(((Device) obj).getDeviceKey())) {
                isEqual = Boolean.TRUE;
            }
        }
        return isEqual;
    }

}
