package cn.gehc.ii.repository;

import cn.gehc.ii.domain.TimerJob;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author 212579464
 */
public interface ReadTimerJobRepository extends PagingAndSortingRepository<TimerJob, String> {
    
    @Query("select o from TimerJob o where o.jobType='ReadDoseDB'")
    List<TimerJob> findAllReadDBJobs();
    
    TimerJob findByJobName(String name);
}
