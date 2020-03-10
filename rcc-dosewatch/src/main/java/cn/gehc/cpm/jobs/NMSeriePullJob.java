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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        StudyKey studyKey;
        NMStudy nmStudy;
        NMSerie nmSerie;
        Long orgId = 0L;
        for(Map<String, Object> serieProps : body) {
            log.debug("series prop: {}", serieProps.toString());

            //retrieve org entity id by facility code
            String facilityCode = DataUtil.getStringFromProperties(serieProps, "facility_code");
            if(orgId.longValue() == 0 && StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                if(orgEntityList.size() > 0) {
                    orgId = orgEntityList.get(0).getOrgId();
                }
                log.info("facility {} is retrieved", orgId);
            }
            if(orgId.longValue() == 0 && StringUtils.isBlank(facilityCode)) {
                log.error("facility hasn't been configured for aet: {}", DataUtil.getStringFromProperties(serieProps, "aet"));
                continue;
            }
            serieProps.put("org_id", orgId);

            study = DataUtil.convertProps2Study(serieProps);
            studySet.add(study);

            studyKey = study.getStudyKey();
            Device device = deviceRepository.findByOrgIdAndAet(orgId, studyKey.getAet());
            if(studyKey.getModality().equals(device.getDeviceKey().getDeviceType())) {
                //device has been saved, DO NOTHING
            } else {
                
            }

        }
    }
}
