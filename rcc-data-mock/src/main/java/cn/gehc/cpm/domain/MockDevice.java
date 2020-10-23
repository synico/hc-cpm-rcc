package cn.gehc.cpm.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "device")
public class MockDevice {

    @EmbeddedId
    private DeviceKey deviceKey;

    @Column(name = "purchase_date")
    private String purchaseDate;

    @Column(name = "purchase_price")
    private Float purchasePrice = 0.0F;

    @Column(name = "salvage_value")
    private Float salvageValue = 0.0F;

    @Column(name = "lifecycle")
    private Integer lifecycle = 1;

    @Column(name = "depreciation_method")
    private Integer depreciationMethod = 1;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

}
