package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.CTStudy;
import cn.gehc.cpm.domain.CTStudyBuilder;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.CTStudyRepository;
import cn.gehc.cpm.repository.MockCTSerieRepository;
import cn.gehc.cpm.utils.CTStudyUtils;
import cn.gehc.cpm.utils.StudyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class CTStudyJob {

    @Autowired
    private CTStudyRepository ctStudyRepository;

    @Autowired
    private MockCTSerieRepository ctSerieRepository;

    public void generateCTStudy(Study study) {
        CTStudy ctStudy = new CTStudy();
        ctStudy.setStudyKey(study.getStudyKey());
        ctStudy.setLocalStudyId(study.getLocalStudyId());
        ctStudy.setNumSeries(CTStudyUtils.generateNumSeries());

        Long protocolKey = CTStudyUtils.generateProtocolKey();
        ctStudy.setProtocolKey(protocolKey);
        ctStudy.setProtocolName(StudyConstant.CT_PROTOCOLS.get(protocolKey));
        ctStudy.setDtLastUpdate(new Date());

        List<CTSerie> serieList = CTStudyBuilder.of(study, getMaxSerieId());
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
