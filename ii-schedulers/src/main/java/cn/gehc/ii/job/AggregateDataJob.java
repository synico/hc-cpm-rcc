package cn.gehc.ii.job;

import cn.gehc.ii.domain.DataStore;
import cn.gehc.ii.repository.DataStoreRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.gehc.ii.util.JobTypeEnum;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cn.gehc.ii.util.JobDefinition.*;

/**
 * @author 212706300
 */

@Service
public class AggregateDataJob {

    private static final Logger log = LoggerFactory.getLogger(AggregateDataJob.class);

    @Autowired
    private DataStoreRepository dataStoreRepository;

    public synchronized void processData(@Headers Map<String, Object> headers, @Body List<Map<String, String>> body) {
        log.info("receive [ {} ] aggregated data", body.size());

        List<DataStore> dataList = new ArrayList<>(body.size());
        String jobType = headers.get(JOB_TYPE.getCode()).toString();

        DataStore dataStore = null;
        for(Map<String, String> aggData : body) {
            dataStore = convertMap2DataStore(headers, aggData);
            dataList.add(dataStore);
        }

        if(dataList.isEmpty()) {
            log.info("No data will be saved");
        } else {
            if (JobTypeEnum.HOURLY.getCode().equals(jobType)) {
                log.info("will deactivate data for jobGroup: {}, jobName: {}, date: {}",
                        dataStore.getJobGroup(), dataStore.getJobName(), dataStore.getColumn1());
                dataStoreRepository.deactivateDataByJobGroupAndName(dataStore.getJobGroup(),
                        dataStore.getJobName(), dataStore.getColumn1());
            } else {
                log.info("will deactivate data for jobGroup: {}, jobName: {}",
                        dataStore.getJobGroup(), dataStore.getJobName());
                dataStoreRepository.deactivateDataByJobGroupAndName(dataStore.getJobGroup(), dataStore.getJobName());
            }
            dataStoreRepository.saveAll(dataList);
        }
    }

    public void cleanData(@Headers Map<String, Object> headers, @Body List<Map<String, String>> body) {
        String jobGroup = headers.get(JOB_GROUP.getCode()).toString();
        String jobName = headers.get(JOB_NAME.getCode()).toString();
        String jobType = headers.get(JOB_TYPE.getCode()).toString();
        String examDay = null;
        if (!body.isEmpty()) {
            DataStore dataStore = convertMap2DataStore(headers, body.get(0));
            examDay = dataStore.getColumn1();
        }

        if (JobTypeEnum.HOURLY.getCode().equals(jobType)) {
            log.info("will clean data for jobGroup: {}, jobName: {}, date: {}", jobGroup, jobName, examDay);
            dataStoreRepository.deleteDataByJobGroupAndName(jobGroup, jobName, examDay);
        } else {
            log.info("will clean data for jobGroup: {}, jobName: {}", jobGroup, jobName);
            dataStoreRepository.deleteDataByJobGroupAndName(jobGroup, jobName);
        }
        log.info("end to clean data for jobGroup: {}, jobName: {}", jobGroup, jobName);
    }

    private DataStore convertMap2DataStore(Map<String, Object> headers, Map<String, String> aggData) {
        DataStore dataStore = new DataStore();

        dataStore.setJobName(headers.get(JOB_NAME.getCode()).toString());
        dataStore.setJobType(headers.get(JOB_TYPE.getCode()).toString());
        dataStore.setJobGroup(headers.get(JOB_GROUP.getCode()).toString());
        dataStore.setLastFireTime((java.util.Date)headers.get(LAST_FIRE_TIME.getCode()));
        //----------------------------------------------------------------
        dataStore.setColumn1(aggData.getOrDefault(COLUMN1.getCode(), null));
        dataStore.setColumn2(aggData.getOrDefault(COLUMN2.getCode(), null));
        dataStore.setColumn3(aggData.getOrDefault(COLUMN3.getCode(), null));
        dataStore.setColumn4(aggData.getOrDefault(COLUMN4.getCode(), null));
        dataStore.setColumn5(aggData.getOrDefault(COLUMN5.getCode(), null));
        dataStore.setColumn6(aggData.getOrDefault(COLUMN6.getCode(), null));
        dataStore.setColumn7(aggData.getOrDefault(COLUMN7.getCode(), null));
        dataStore.setColumn8(aggData.getOrDefault(COLUMN8.getCode(), null));
        dataStore.setColumn9(aggData.getOrDefault(COLUMN9.getCode(), null));

        return dataStore;
    }

}
