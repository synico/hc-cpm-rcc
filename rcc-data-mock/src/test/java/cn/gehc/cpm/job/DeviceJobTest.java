package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.Device;
import cn.gehc.cpm.repository.DeviceRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.gehc.cpm.utils.DeviceConstant.MFCode;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceJobTest {

    @Autowired
    private DeviceJob job;

    @Autowired
    private DeviceRepository deviceRepository;

    @Before
    public void setUp() throws Exception{
    }

    @Test
    public void addDevices() {
        List<Device> deviceList = job.addDevices();
        assert deviceList.size() == 9 : "Don't have enough devices";
        Assert.assertEquals(deviceList.size(), 9);
        List<String> mfCodeList = Stream.of(MFCode.GE.name(), MFCode.SIEMENS.name(), MFCode.PHILIPS.name())
                .collect(Collectors.toList());
        for(Device device : deviceList) {
            assert mfCodeList.contains(device.getMfCode()) : "unknow manufacturer";
        }

        Iterable<Device> devices = deviceRepository.findAll();
        devices.forEach(d -> System.out.println(d.getDeviceKey().getAet()));
    }
}