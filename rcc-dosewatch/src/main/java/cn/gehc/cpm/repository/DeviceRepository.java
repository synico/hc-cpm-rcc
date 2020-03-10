package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.Device;
import cn.gehc.cpm.domain.DeviceKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface DeviceRepository extends PagingAndSortingRepository<Device, String> {

    Device findByDeviceKey(DeviceKey deviceKey);

    @Query(value = "select * from device where org_id = ?1 and aet = ?2", nativeQuery = true)
    List<Device> findByOrgIdAndAet(Long orgId, String aet);

}
