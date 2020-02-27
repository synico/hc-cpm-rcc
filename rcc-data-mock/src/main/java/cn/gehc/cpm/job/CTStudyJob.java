package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.CTStudy;
import cn.gehc.cpm.domain.CTStudyBuilder;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.CTStudyRepository;
import cn.gehc.cpm.repository.MockCTSerieRepository;
import cn.gehc.cpm.utils.CTStudyUtils;
import cn.gehc.cpm.utils.DeviceConstant;
import cn.gehc.cpm.utils.StudyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
public class CTStudyJob {

    private static final Logger log = LoggerFactory.getLogger(CTStudyJob.class);

    @Autowired
    private StudyJob studyJob;

    @Autowired
    private CTStudyRepository ctStudyRepository;

    @Autowired
    private MockCTSerieRepository ctSerieRepository;


    @Schedules(value = {
            @Scheduled(cron = "0 0/5 7-18 * * MON-FRI"),
            @Scheduled(cron = "0 0/5 7-13 * * SUN,SAT")
    })
    public void generateCTStudies() {
        log.info("start to generate CT studies at {}", LocalDateTime.now());

        for(DeviceConstant.AE ae : DeviceConstant.CT_LIST) {
            Study study = studyJob.createStudy(ae);
            createCTStudy(study);
        }
    }

    private void createCTStudy(Study study) {
        CTStudy ctStudy = new CTStudy();
        ctStudy.setStudyKey(study.getStudyKey());
        ctStudy.setLocalStudyId(study.getLocalStudyId());

        Long protocolKey = CTStudyUtils.generateProtocolKey();
        ctStudy.setProtocolKey(protocolKey);
        ctStudy.setProtocolName(StudyConstant.CT_PROTOCOLS.get(protocolKey));
        ctStudy.setDtLastUpdate(new Date());

        List<CTSerie> serieList = CTStudyBuilder.of(study, getMaxSerieId());
        ctStudy.setNumSeries(serieList.size());
        if(serieList.size() > 0) {
            ctSerieRepository.saveAll(serieList);
        }

        ctStudyRepository.save(ctStudy);
    }

    private Long getMaxSerieId() {
        Long maxSerieId = ctSerieRepository.getMaxSerieId();
        if(maxSerieId == null) {
            maxSerieId = 0L;
        }
        return maxSerieId;
    }

}
