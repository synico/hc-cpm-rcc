package cn.gehc.ii.repository;

import cn.gehc.ii.domain.DataStore;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author 212706300
 */
public interface DataStoreRepository extends PagingAndSortingRepository<DataStore, Integer> {

    @Modifying
    @Transactional
    @Query("update DataStore ds set ds.isActive = false where ds.jobGroup = ?1")
    int deactivateDataByJobGroup(String jobGroup);

    @Modifying
    @Transactional
    @Query("update DataStore ds set ds.isActive = false where ds.jobGroup = ?1 and ds.jobName = ?2")
    int deactivateDataByJobGroupAndName(String jobGroup, String jobName);

    @Modifying
    @Transactional
    @Query("update DataStore ds set ds.isActive = false where ds.jobGroup = ?1 and ds.jobName = ?2 and ds.column1 = ?3")
    int deactivateDataByJobGroupAndName(String jobGroup, String jobName, String examDay);

    @Modifying
    @Transactional
    @Query("delete from DataStore ds where ds.isActive = false and ds.jobGroup = ?1")
    int deleteDataByJobGroup(String jobGroup);

    @Modifying
    @Transactional
    @Query("delete from DataStore ds where ds.isActive = false and ds.jobGroup = ?1 and ds.jobName = ?2")
    int deleteDataByJobGroupAndName(String jobGroup, String jobName);

    @Modifying
    @Transactional
    @Query("delete from DataStore ds where ds.isActive = false and ds.jobGroup = ?1 and ds.jobName = ?2 and ds.column1 = ?3")
    int deleteDataByJobGroupAndName(String jobGroup, String jobName, String examDay);

}
