/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.TimerJob;
import cn.gehc.cpm.repository.ReadTimerJobRepository;
import org.apache.camel.Headers;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author 212579464
 */

@Service
public class TimerDBReadJob {

    @Autowired
    protected ReadTimerJobRepository jobDao;

    public void startDBReadJob(@Headers Map<String, Object> headers) {
        TimerJob job = jobDao.findByJobName(headers.get("JobName").toString());
        if (null == job || Strings.isEmpty(job.getLastPolledValue())) {
            job = initJobObject(headers);
        }
        job.setStatus(TimerJob.JobStatus.Running.toString());
        job.setLastStartTime(new Date());
        jobDao.save(job);
        headers.put("JobObject", job);
    }

    public void endDBReadJob(@Headers Map<String, Object> headers) {
        TimerJob job = jobDao.findByJobName(headers.get("JobName").toString());
        job.setStatus(TimerJob.JobStatus.End.toString());
        jobDao.save(job);
    }

    protected TimerJob initJobObject(@Headers Map<String, Object> headers){
        TimerJob job = new TimerJob();
        job.setJobName(headers.get("JobName").toString());
        job.setJobType(TimerJob.JobType.ReadDoseDB.toString());
        job.setStatus(TimerJob.JobStatus.End.toString());
        job.setLastPolledValue(headers.get("DefaultStartPollValue").toString());
        return job;
    }
    
    protected void updateLastPullValue(Map<String, Object> headers , String value){
        TimerJob job = (TimerJob)headers.get("JobObject");
        job.setLastPolledValue(value);
        job.setLastUpdatedTime(new Date());
        jobDao.save(job);
    }
    
}
