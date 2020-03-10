package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.Device;
import cn.gehc.cpm.domain.DeviceKey;
import cn.gehc.cpm.domain.OrgEntity;
import cn.gehc.cpm.repository.DeviceRepository;
import cn.gehc.cpm.util.DataUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DevicePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(DevicePullJob.class);

    @Autowired
    private DeviceRepository deviceRepository;

    public void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to insert data to device");

        List<Device> deviceList = new ArrayList<>();

        Date lastPolledValue = null;
        Device device;
        Long orgId = 0L;
        for(Map<String, Object> deviceProps : body) {
            log.debug(deviceProps.toString());

            String facilityCode = DataUtil.getStringFromProperties(deviceProps, "facility_code");
            if(StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                OrgEntity orgEntity = (orgEntityList.size() == 1) ? orgEntityList.get(0) : new OrgEntity();
                if(orgEntity.getOrgId() != null) {
                    orgId = orgEntity.getOrgId();
                } else {
                    orgEntity.setOrgName(facilityCode);
                    orgEntity.setCreateTime(Date.from(Instant.now()));
                    OrgEntity savedOrgEntity = orgEntityRepository.save(orgEntity);
                    orgId = savedOrgEntity.getOrgId();
                }
            }

            String aet = DataUtil.getStringFromProperties(deviceProps, "aet");
            String deviceType = DataUtil.getStringFromProperties(deviceProps, "device_type");
            DeviceKey deviceKey = new DeviceKey();
            deviceKey.setOrgId(orgId);
            deviceKey.setAet(aet);
            deviceKey.setDeviceType(deviceType);

            Long id = DataUtil.getLongFromProperties(deviceProps, "id");
            String deviceModel = DataUtil.getStringFromProperties(deviceProps, "device_model");
            String mfCode = DataUtil.getStringFromProperties(deviceProps, "mf_code");
            String name = DataUtil.getStringFromProperties(deviceProps, "name");
            String stationName = DataUtil.getStringFromProperties(deviceProps, "station_name");
            String timezone = DataUtil.getStringFromProperties(deviceProps, "timezone");
            Date dtLastUpdate = DataUtil.getDateFromProperties(deviceProps, "dt_last_update");

            Device optionalDevice = deviceRepository.findByDeviceKey(deviceKey);
            if(optionalDevice != null) {
                device = optionalDevice;
            } else {
                device = new Device();
                device.setDeviceKey(deviceKey);
            }
            log.debug("Retrieve device id: {} from dosewatch", id);
            device.setId(id);
            device.setDeviceModel(deviceModel);
            device.setMfCode(mfCode);
            device.setName(name);
            device.setStationName(stationName);
            device.setTimezone(timezone);
            device.setDtLastUpdate(dtLastUpdate);
            device.setCreateTime(Date.from(Instant.now()));

            deviceList.add(device);

            if(lastPolledValue == null) {
                lastPolledValue = dtLastUpdate;
            } else {
                lastPolledValue = lastPolledValue.compareTo(dtLastUpdate) > 0 ? lastPolledValue : dtLastUpdate;
            }
        }

        if(deviceList.size() > 0) {
            deviceRepository.saveAll(deviceList);
        }

        log.info("last polled value: {}", lastPolledValue);

        if(lastPolledValue != null) {
            super.updateLastPullValue(headers, DataUtil.convertDate2String(lastPolledValue, DataUtil.DatabaseType.POSTGRES));
        }
    }

}
