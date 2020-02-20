package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.MRSerie;
import cn.gehc.cpm.domain.MRStudy;
import cn.gehc.cpm.domain.MRStudyBuilder;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.MRStudyRepository;
import cn.gehc.cpm.repository.MockMRSerieRepository;
import cn.gehc.cpm.utils.MRStudyUtils;
import cn.gehc.cpm.utils.StudyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MRStudyJob {

    @Autowired
    private MRStudyRepository mrStudyRepository;

    @Autowired
    private MockMRSerieRepository mrSerieRepository;

    public void generateMRStudy(Study study) {
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
