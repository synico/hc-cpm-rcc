package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.Device;
import cn.gehc.cpm.domain.DeviceBuilder;
import cn.gehc.cpm.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.gehc.cpm.utils.DeviceConstant.*;

@Component
public class DeviceJob {

    @Autowired
    private DeviceRepository deviceRepository;

    @Scheduled(cron = "0 0/30 7-23 * * ?")
    public List<Device> addDevices() {
        long count = deviceRepository.count();
        List<Device> deviceList = null;
        if(count > 0) {
            //do nothing
        } else {
            deviceList = initDevices();
            deviceRepository.saveAll(deviceList);
        }
        return deviceList;
    }

    private List<Device> initDevices() {
        List<Device> deviceList = new ArrayList<>();

        DeviceBuilder deviceBuilder = new DeviceBuilder();

        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();

        /******************CT******************/
        //SIEMENS
        Device ct1 = deviceBuilder.of(AE.CT1.getAet(), AE.CT1.getAeKey(), "SOMATOM Definition AS",
                Type.CT.name(), Date.from(instant.minus(1L, ChronoUnit.MINUTES)), MFCode.SIEMENS.name(),
                "128#CT", "CTAWP2020");
        deviceList.add(ct1);
        //GE
        Device ct2 = deviceBuilder.of(AE.CT2.getAet(), AE.CT2.getAeKey(), "Discovery CT750 HD",
                Type.CT.name(), Date.from(instant.minus(2L, ChronoUnit.MINUTES)), MFCode.GE.name(),
                "CT750", "ct750");
        deviceList.add(ct2);
        //PHILIPS
        Device ct3 = deviceBuilder.of(AE.CT3.getAet(), AE.CT3.getAeKey(), "MX 16-Slice",
                Type.CT.name(), Date.from(instant.minus(3L, ChronoUnit.MINUTES)), MFCode.PHILIPS.name(),
                "CT16", "ct16");
        deviceList.add(ct3);

        /******************MR******************/
        //SIEMENS
        Device mr1 = deviceBuilder.of(AE.MR1.getAet(), AE.MR1.getAeKey(), "MAGNETOM Aera 1.5T",
                Type.MR.name(), Date.from(instant.minus(4L, ChronoUnit.MINUTES)), MFCode.SIEMENS.name(),
                "MR1", "AWP202002");
        deviceList.add(mr1);
        //GE
        Device mr2 = deviceBuilder.of(AE.MR2.getAet(), AE.MR2.getAeKey(), "Discovery MR750 3.0T",
                Type.MR.name(), Date.from(instant.minus(5L, ChronoUnit.MINUTES)), MFCode.GE.name(),
                "MR2", "GEHC");
        deviceList.add(mr2);

        /******************XA******************/
        //GE
        Device xa1 = deviceBuilder.of(AE.XA1.getAet(), AE.XA1.getAeKey(), "Innova IGS 520",
                Type.XA.name(), Date.from(instant.minus(6L, ChronoUnit.MINUTES)), MFCode.GE.name(),
                "GEXA1", "IGS520");
        deviceList.add(xa1);
        Device xa2 = deviceBuilder.of(AE.XA2.getAet(), AE.XA2.getAeKey(), "Innova IGS 540",
                Type.XA.name(), Date.from(instant.minus(7L, ChronoUnit.MINUTES)), MFCode.GE.name(),
                "GEXA2", "TERRA2020");
        deviceList.add(xa2);

        /******************RF******************/
        //SIEMENS
        Device rf1 = deviceBuilder.of(AE.RF1.getAet(), AE.RF1.getAeKey(), "AXIOM Aristos MX",
                Type.RF.name(), Date.from(instant.minus(8L, ChronoUnit.MINUTES)), MFCode.SIEMENS.name(),
                "DR1", "RADIS01");
        deviceList.add(rf1);
        //GE
        Device rf2 = deviceBuilder.of(AE.RF2.getAet(), AE.RF2.getAeKey(), "Discovery XR656",
                Type.RF.name(), Date.from(instant.minus(8L, ChronoUnit.MINUTES)), MFCode.GE.name(),
                "DR2", "20200210");
        deviceList.add(rf2);

        return deviceList;
    }

}
