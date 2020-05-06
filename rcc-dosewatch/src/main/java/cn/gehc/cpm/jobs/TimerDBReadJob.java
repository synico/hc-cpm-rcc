/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.DeviceKey;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.domain.TimerJob;
import cn.gehc.cpm.repository.OrgEntityRepository;
import cn.gehc.cpm.repository.ReadTimerJobRepository;
import cn.gehc.cpm.repository.StudyRepository;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */

public abstract class TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(TimerDBReadJob.class);

    @Autowired
    protected ReadTimerJobRepository jobDao;

    @Autowired
    protected StudyRepository studyRepository;

    @Autowired
    protected OrgEntityRepository orgEntityRepository;

    public void startDBReadJob(@Headers Map<String, Object> headers) {
        TimerJob job = jobDao.findByJobName(headers.get("JobName").toString());
        if (null == job || Strings.isEmpty(job.getLastPolledValue())) {
            job = initJobObject(headers);
        }
        job.setStatus(TimerJob.JobStatus.Running.toString());
        job.setLastStartTime(new Date());
        jobDao.save(job);
        headers.put("JobObject", job);
        Thread currentThread = Thread.currentThread();
        log.info("Thread id: {}, name: {}", currentThread.getId(), currentThread.getName());
    }

    public void endDBReadJob(@Headers Map<String, Object> headers) {
        TimerJob job = jobDao.findByJobName(headers.get("JobName").toString());
        job.setStatus(TimerJob.JobStatus.End.toString());
        jobDao.save(job);
    }

    protected TimerJob initJobObject(@Headers Map<String, Object> headers){
        TimerJob job = new TimerJob();
        job.setJobName(headers.get("JobName").toString());
        Object jobType = headers.get("JobType");
        if(jobType != null) {
            job.setJobType(jobType.toString());
        }
        job.setStatus(TimerJob.JobStatus.End.toString());
        job.setLastPolledValue(headers.get("DefaultStartPollValue").toString());
        return job;
    }

    /**
     * method to process incoming data and return output
     * @param headers
     * @param body
     */
    public abstract void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body);
    
    protected void updateLastPullValue(Map<String, Object> headers , String value){
        TimerJob job = (TimerJob)headers.get("JobObject");
        job.setLastPolledValue(value);
        job.setLastUpdatedTime(new Date());
        jobDao.save(job);
    }

    /**
     * As some studies have been persisted to database in previous job, to avoid values of study to be
     * overwritten, need to merge studies from job and database.
     * @param studiesFromJob
     * @return merged studies
     * @since v1.1
     */
    protected Set<Study> mergeStudies(Set<Study> studiesFromJob) {
        List<String> studyIds = studiesFromJob.stream().map(s -> s.getLocalStudyId()).collect(Collectors.toList());
        List<Study> studyFromDb = studyRepository.findByLocalStudyIdIn(studyIds);
        studiesFromJob.stream().filter(s -> !studyFromDb.contains(s)).forEach(studyFromDb::add);
        return new HashSet<>(studyFromDb);
    }

    public void linkStudies(Collection<Study> study2Update) {
        // update prev_local_study_id and next_local_study_id
        Map<DeviceKey, Set<String>> aetStudyDateMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DeviceKey deviceKey = null;
        for (Study s : study2Update) {
            deviceKey = new DeviceKey();
            deviceKey.of(s.getStudyKey().getOrgId(), s.getStudyKey().getAet(), s.getStudyKey().getModality());
            Set studyDateSet = aetStudyDateMap.get(deviceKey);
            if (studyDateSet == null) {
                studyDateSet = new HashSet();
            }
            studyDateSet.add(sdf.format(s.getStudyDate()));
            aetStudyDateMap.put(deviceKey, studyDateSet);
        }
        for (Map.Entry<DeviceKey, Set<String>> aetStudyDate : aetStudyDateMap.entrySet()) {
            DeviceKey aeKey = aetStudyDate.getKey();
            Set<String> studyDateSet = aetStudyDate.getValue();
            for (String studyDateString : studyDateSet) {
                List<Study> studies = studyRepository.findByAEAndStudyDateChar(aeKey.getOrgId(),
                        aeKey.getAet(), aeKey.getDeviceType(), studyDateString);
                Study prevStudy = (studies != null && studies.size() > 0) ? studies.get(0) : null;
                Study currentStudy = null;
                for (int idx = 1; idx < studies.size(); idx++) {
                    currentStudy = studies.get(idx);
                    prevStudy.setNextLocalStudyId(currentStudy.getLocalStudyId());
                    currentStudy.setPrevLocalStudyId(prevStudy.getLocalStudyId());
                    prevStudy = currentStudy;
                }
                studyRepository.saveAll(studies);
            }
        }
    }
    
}
