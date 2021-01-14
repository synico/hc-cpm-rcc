package cn.gehc.ii.repository;

import cn.gehc.ii.domain.DeviceScheduleKey;
import cn.gehc.ii.domain.DeviceWorkingSchedule;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author 212706300
 */

public interface DeviceWorkingScheduleRepository extends PagingAndSortingRepository<DeviceWorkingSchedule, DeviceScheduleKey> {
}
