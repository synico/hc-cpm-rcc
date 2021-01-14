package cn.gehc.ii;

import cn.gehc.cpm.domain.Device;
import cn.gehc.cpm.repository.DeviceRepository;
import cn.gehc.cpm.repository.StudyRepository;
import cn.gehc.ii.domain.DeviceScheduleKey;
import cn.gehc.ii.domain.DeviceWorkingSchedule;
import cn.gehc.ii.repository.DeviceWorkingScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j(topic = "DeviceWorkingScheduleTest")
@RunWith(SpringRunner.class)
@SpringBootTest
@EnableJpaRepositories(basePackages = "cn.gehc.*")
@EntityScan(basePackages = "cn.gehc.*")
public class DeviceWorkingScheduleTest {

    @Autowired
    private DeviceWorkingScheduleRepository deviceWorkingScheduleRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Test
    public void buildScheudles() {
        log.info("start to build schedules");
        List<DeviceWorkingSchedule> deviceWorkingSchedules = new ArrayList<>();

        Iterable<Device> deviceList = deviceRepository.findAll();
        deviceList.forEach(device -> {
            LocalDate startDate = LocalDate.of(2020, 9, 1);
            Random random = new Random();
            startDate = startDate.plusDays(random.nextInt(20));
            DeviceWorkingSchedule deviceWorkingSchedule = new DeviceWorkingSchedule();
            DeviceScheduleKey key = new DeviceScheduleKey();
            key.of(device.getDeviceKey().toString(), startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            log.info("device key: {}, start date: {}", key.getDeviceKey(), key.getStartDate());
            deviceWorkingSchedule.setDeviceScheduleKey(key);
            deviceWorkingSchedule.setOpeningHours("09:00");
            deviceWorkingSchedule.setClosingHours("20:00");
            deviceWorkingSchedules.add(deviceWorkingSchedule);
        });
        deviceWorkingScheduleRepository.saveAll(deviceWorkingSchedules);
    }

    public void getDevices() {
    }
}
