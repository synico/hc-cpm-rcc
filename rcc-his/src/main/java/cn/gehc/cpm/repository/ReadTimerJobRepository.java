/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.TimerJob;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 *
 * @author 212579464
 */
public interface ReadTimerJobRepository extends PagingAndSortingRepository<TimerJob, String> {
    
    @Query("select o from TimerJob o where o.jobType='ReadDoseDB'")
    List<TimerJob> findAllReadDBJobs();
    
    TimerJob findByJobName(String name);
}
