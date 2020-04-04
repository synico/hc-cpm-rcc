package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.*;
import cn.gehc.cpm.repository.DeviceRepository;
import cn.gehc.cpm.repository.NMSerieRepository;
import cn.gehc.cpm.repository.NMStudyRepository;
import cn.gehc.cpm.util.DataUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(value = "nmSeriePullJob")
public class NMSeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(NMSeriePullJob.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private NMStudyRepository nmStudyRepository;

    @Autowired
    private NMSerieRepository nmSerieRepository;

    public void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to insert data to nm_serie");

        Set<Study> studySet = new HashSet<>();
        Set<NMStudy> nmStudySet = new HashSet<>();
        Set<NMSerie> nmSerieSet = new HashSet<>();

        Long lastPolledValue = null;
        Study study;
        NMStudy nmStudy;
        NMSerie nmSerie;
        Long orgId = 0L;
        DeviceKey deviceKey = null;
        for(Map<String, Object> serieProps : body) {
            log.debug("series prop: {}", serieProps.toString());

            //retrieve org entity id by facility code
            String facilityCode = DataUtil.getStringFromProperties(serieProps, "facility_code");
            if(orgId.longValue() == 0 && StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                if(orgEntityList.size() > 0) {
                    orgId = orgEntityList.get(0).getOrgId();
                    log.info("facility {} is retrieved", orgId);
                } else {
                    // !!! IMPORTANT !!! job will not save data to database while org_entity has not been set
                    log.warn("The org/device has not been synchronized, job will not save data");
                    return;
                }
            }
            if(orgId.longValue() == 0 && StringUtils.isBlank(facilityCode)) {
                log.error("facility hasn't been configured for aet: {}", DataUtil.getStringFromProperties(serieProps, "aet"));
                continue;
            }
            serieProps.put("org_id", orgId);

            study = DataUtil.convertProps2Study(serieProps);
            studySet.add(study);

            // FOR some ECT/PET we need to create device there is only one aet in dw
            log.info("device key is: {}", deviceKey);
            if(deviceKey == null) {
                StudyKey studyKey = study.getStudyKey();
                List<Device> deviceList = deviceRepository.findByOrgIdAndAet(orgId, studyKey.getAet());
                Device ectDevice = null;
                for(Device device : deviceList) {
                    if(study.getStudyKey().getOrgId().equals(device.getDeviceKey().getOrgId()) &&
                        studyKey.getAet().equals(device.getDeviceKey().getAet())) {
                        log.info("retrieved device: {}, studyKey in study: {}", device.getDeviceKey(), study.getStudyKey());
                        if(studyKey.getModality().equals(device.getDeviceKey().getDeviceType())) {
                            // device(CT and NM) has been saved
                            deviceKey = device.getDeviceKey();
                            log.info("device key has been found: {}", deviceKey.toString());
                        } else {
                            // ONLY CT of ECT has been saved
                            ectDevice = device;
                            log.warn("only CT has been saved, need to create NM");
                        }
                    }
                }
                // device has not been saved
                log.info("deviceKey: {}, ectDevice: {}", deviceKey, ectDevice);
                if(deviceKey == null && ectDevice != null) {
                    DeviceKey nmDeviceKey = new DeviceKey();
                    BeanUtils.copyProperties(ectDevice.getDeviceKey(), nmDeviceKey);
                    nmDeviceKey.setDeviceType(studyKey.getModality());
                    Device nmDevice = new Device();
                    BeanUtils.copyProperties(ectDevice, nmDevice, "deviceKey");
                    nmDevice.setDeviceKey(nmDeviceKey);
                    nmDevice.setCreateTime(Date.from(Instant.now()));
                    deviceRepository.save(nmDevice);
                    deviceKey = nmDeviceKey;
                    log.info("new device has been added to II: {}", deviceKey.toString());
                }
            }

            nmStudy = DataUtil.convertProps2NMStudy(serieProps);
            nmStudySet.add(nmStudy);

            nmSerie = DataUtil.convertProps2NMSerie(serieProps);
            nmSerieSet.add(nmSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            if(lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }

        }

        if(studySet.size() > 0) {
            studyRepository.saveAll(studySet);
        }

        if(nmStudySet.size() > 0) {
            nmStudyRepository.saveAll(nmStudySet);
        }

        if(nmSerieSet.size() > 0) {
            nmSerieRepository.saveAll(nmSerieSet);
        }

        linkStudies(studySet);

        if (lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }
}
