package cn.gehc.ii.job;

import cn.gehc.ii.domain.DataStore;
import cn.gehc.ii.repository.DataStoreRepository;
import cn.gehc.ii.util.JobTypeEnum;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static cn.gehc.ii.util.JobDefinition.*;

/**
 * @author 212706300
 */

@Service
public class DeviceWorkingloadByDayJob {

    private static final Logger log = LoggerFactory.getLogger(DeviceWorkingloadByDayJob.class);

    @Autowired
    private DataStoreRepository dataStoreRepository;

    private static final DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter yearMonthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

    public void processData(@Headers Map<String, Object> headers, @Body List<Map<String, String>> body) {
        log.info("receive [ {} ] aggregated data", body.size());

        List<DataStore> dataList = new ArrayList<>(body.size());
        String jobType = headers.get(JOB_TYPE.getCode()).toString();

        DataStore dataStore = null;
        for (Map<String, String> aggData : body) {
            dataStore = convertMap2DataStore(headers, aggData);
            dataList.add(dataStore);
        }

        if (dataList.isEmpty()) {
            log.info("No data will be saved");
        } else {
            if (JobTypeEnum.YEARLY.getCode().equals(jobType)) {
                log.info("for historical data, the job will be processed only once");
                dataStoreRepository.deactivateDataByJobGroupAndName(dataStore.getJobGroup(), dataStore.getJobName());
            } else {
                log.info("for the day's study, will be updated hourly");
                dataStoreRepository.deactivateDataByJobGroupNameAndType(dataStore.getJobGroup(),
                        dataStore.getJobName(), dataStore.getJobType());
            }
            dataStoreRepository.saveAll(dataList);
        }
    }

    public void cleanData(@Headers Map<String, Object> headers, @Body List<Map<String, String>> body) {
        String jobGroup = headers.get(JOB_GROUP.getCode()).toString();
        String jobName = headers.get(JOB_NAME.getCode()).toString();

        log.info("will clean data for jobGroup: {}, jobName: {}", jobGroup, jobName);
        dataStoreRepository.deleteDataByJobGroupAndName(jobGroup, jobName);
        log.info("end to clean data for jobGroup: {}, jobName: {}", jobGroup, jobName);
    }

    private DataStore convertMap2DataStore(Map<String, Object> headers, Map<String, String> aggData) {
        DataStore dataStore = new DataStore();

        dataStore.setJobName(headers.get(JOB_NAME.getCode()).toString());
        dataStore.setJobType(headers.get(JOB_TYPE.getCode()).toString());
        dataStore.setJobGroup(headers.get(JOB_GROUP.getCode()).toString());
        dataStore.setLastFireTime(Date.from(Instant.now()));
        //----------------------------------------------------------------
        dataStore.setColumn1(aggData.getOrDefault("device_key", null));

        String studyDateStr = aggData.getOrDefault("study_date_str", "1970-01-01");
        dataStore.setColumn2(studyDateStr);
        LocalDate studyDate = LocalDate.parse(studyDateStr, dateFormatter);

        // working time(column3)
        String openingHoursStr = aggData.get("opening_hours");
        String closingHoursStr = aggData.get("closing_hours");

        LocalTime openingHours = LocalTime.parse(openingHoursStr, hourFormatter);
        LocalTime closingHours = LocalTime.parse(closingHoursStr, hourFormatter);

        Duration workingTime = Duration.between(openingHours, closingHours);
        dataStore.setColumn3(Long.toString(workingTime.getSeconds()));

        dataStore.setColumn4(aggData.getOrDefault("study_time", null));
        // study month(yyyy-MM)(column5)
        dataStore.setColumn5(studyDate.format(yearMonthFormatter));
        //day of week(column6)
        dataStore.setColumn6(Integer.toString(studyDate.getDayOfWeek().getValue()));
        // num of study
        dataStore.setColumn7(aggData.getOrDefault("study_total", "0"));

        return dataStore;
    }

}
