package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.MRSerie;
import cn.gehc.cpm.domain.MRStudy;
import cn.gehc.cpm.domain.MRStudyBuilder;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.MRStudyRepository;
import cn.gehc.cpm.repository.MockMRSerieRepository;
import cn.gehc.cpm.utils.DeviceConstant;
import cn.gehc.cpm.utils.MRStudyUtils;
import cn.gehc.cpm.utils.StudyConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MRStudyJob {

    private static final Logger log = LoggerFactory.getLogger(MRStudyJob.class);

    @Autowired
    private StudyJob studyJob;

    @Autowired
    private MRStudyRepository mrStudyRepository;

    @Autowired
    private MockMRSerieRepository mrSerieRepository;

    @Schedules(value = {
            @Scheduled(cron = "0 0/15 7-18 * * MON-FRI"),
            @Scheduled(cron = "0 0/15 7-13 * * SUN,SAT")
    })
    public void generateMRStudies() {
        log.info("start to generate MR studies at {}", LocalDateTime.now());

        for(DeviceConstant.AE ae : DeviceConstant.MR_LIST) {
            Study study = studyJob.createStudy(ae);
            createMRStudy(study);
        }
    }

    private void createMRStudy(Study study) {
        MRStudy mrStudy = new MRStudy();
        mrStudy.setStudyKey(study.getStudyKey());
        mrStudy.setLocalStudyId(study.getLocalStudyId());

        Long protocolKey = MRStudyUtils.generateProtocolKey();
        mrStudy.setProtocolKey(protocolKey);
        mrStudy.setProtocolName(StudyConstant.MR_PROTOCOLS.get(protocolKey));

        List<MRSerie> serieList = MRStudyBuilder.of(study, getMaxSerieId());
        if(serieList.size() > 0) {
            mrSerieRepository.saveAll(serieList);
        }

        mrStudyRepository.save(mrStudy);
    }

    private Long getMaxSerieId() {
        Long maxSerieId = mrSerieRepository.getMaxSerieId();
        if(maxSerieId == null) {
            maxSerieId = 0L;
        }
        return maxSerieId;
    }

}
