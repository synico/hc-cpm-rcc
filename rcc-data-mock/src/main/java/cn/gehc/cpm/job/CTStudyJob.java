package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.CTStudy;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.CTSerieRepository;
import cn.gehc.cpm.repository.CTStudyRepository;
import cn.gehc.cpm.utils.CTStudyUtils;
import cn.gehc.cpm.utils.StudyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CTStudyJob {

    @Autowired
    private CTStudyRepository ctStudyRepository;

    @Autowired
    private CTSerieRepository ctSerieRepository;

    public void generateCTStudies(Study study) {
        CTStudy ctStudy = new CTStudy();
        ctStudy.setStudyKey(study.getStudyKey());
        ctStudy.setLocalStudyId(study.getLocalStudyId());
        ctStudy.setNumSeries(CTStudyUtils.generateNumSeries());

        Long protocolKey = CTStudyUtils.generateProtocolKey();
        ctStudy.setProtocolKey(protocolKey);
        ctStudy.setProtocolName(StudyConstant.CT_PROTOCOLS.get(protocolKey));
        ctStudy.setDtLastUpdate(new Date());

        ctStudyRepository.save(ctStudy);
    }

    public void generateCTSeries() {

    }

}
