package cn.gehc.cpm.domain;

import cn.gehc.cpm.utils.DeviceConstant;

import java.util.Date;

public class DeviceBuilder {

    public Device of(String aet, Long dk, String model, String type, Date lastupdate,
                     String mfCode, String name, String stationName) {
        DeviceKey deviceKey = new DeviceKey();
        deviceKey.setAet(aet);
        deviceKey.setId(dk);

        Device device = new Device();
        device.setDeviceKey(deviceKey);
        device.setDeviceModel(model);
        device.setDeviceType(type);
        device.setDtLastUpdate(lastupdate);
        device.setMfCode(mfCode);
        device.setName(name);
        device.setStationName(stationName);
        device.setTimezone(DeviceConstant.TIMEZONE);

        return device;
    }

}
