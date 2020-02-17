package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.MRStudy;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.MRStudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MRStudyJob {

    @Autowired
    private MRStudyRepository mrStudyRepository;

    public void generateMRStudy(Study study) {
        MRStudy mrStudy = new MRStudy();
        mrStudy.setStudyKey(study.getStudyKey());
        mrStudy.setLocalStudyId(study.getLocalStudyId());

        //TODO
//        mrStudyRepository.save(mrStudy);
    }

}
