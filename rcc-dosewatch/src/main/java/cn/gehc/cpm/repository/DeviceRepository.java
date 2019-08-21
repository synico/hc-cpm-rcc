package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.Device;
import cn.gehc.cpm.domain.DeviceKey;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DeviceRepository extends PagingAndSortingRepository<Device, String> {

    Device findByDeviceKey(DeviceKey deviceKey);
}
