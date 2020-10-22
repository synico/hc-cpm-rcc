package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.domain.StudyKey;
import cn.gehc.cpm.repository.MockStudyRepository;
import cn.gehc.cpm.utils.DeviceConstant;
import cn.gehc.cpm.utils.StudyConstant;
import cn.gehc.cpm.utils.StudyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static cn.gehc.cpm.utils.DeviceConstant.CT;
import static cn.gehc.cpm.utils.DeviceConstant.MR;

@Component
public class StudyJob {

    @Autowired
    private MockStudyRepository studyRepository;

    public synchronized Study createStudy(DeviceConstant.AE ae) {
        List<Study> study2Update = new ArrayList<>(2);

        Long currentMaxStudyId = this.getMaxStudyId();
        StudyKey studyKey = new StudyKey();
        studyKey.setOrgId(DeviceConstant.ORG_ID);
        studyKey.setAet(ae.getAet());
        studyKey.setModality(ae.getType());
        studyKey.setDeviceKey(ae.getDeviceKey(studyKey.getOrgId()));
        studyKey.setId(currentMaxStudyId + 1);

        Study study = new Study();
        study.setStudyKey(studyKey);

        switch (ae.getType()) {
            case CT:
                study.setDType(StudyConstant.Type.CTSTUDY.getName());
                study.setTargetRegionCount(StudyUtils.generateTargetRegionCount(StudyConstant.Type.CTSTUDY));
                study.setStudyDescription(StudyUtils.generateStudyDesc(StudyConstant.Type.CTSTUDY));
                break;
            case MR:
                study.setDType(StudyConstant.Type.MRSTUDY.getName());
                study.setTargetRegionCount(StudyUtils.generateTargetRegionCount(StudyConstant.Type.MRSTUDY));
                study.setStudyDescription(StudyUtils.generateStudyDesc(StudyConstant.Type.MRSTUDY));
                break;
        }

        study.setAccessionNumber(StudyUtils.generateAccessionNumber(ae.name()));
        study.setDtLastUpdate(new Date());
        study.setHasRepeatedSeries(Boolean.FALSE);
        study.setLocalStudyId(studyKey.getDeviceKey() + "|" + studyKey.getId());
        study.setPatientAge(StudyUtils.generatePatientAge());
        study.setPatientId(StudyUtils.generatePatientId());
        study.setPatientSex(StudyUtils.generatePatientSex());
        study.setPublished(1);

        Date studyDate = StudyUtils.generateStudyDate();
        study.setStudyDate(studyDate);
        study.setStudyStartTime(StudyUtils.generateStudyStartTime(studyDate.toInstant()));
        study.setStudyEndTime(StudyUtils.generateStudyEndTime(studyDate.toInstant(), ae.getType()));

        // update previous study and current study
        Study prevStudy = this.findPrevStudy(study);
        if(prevStudy != null) {
            prevStudy.setNextLocalStudyId(study.getLocalStudyId());
            study.setPrevLocalStudyId(prevStudy.getLocalStudyId());
            study2Update.add(prevStudy);
        }

        study2Update.add(study);

        studyRepository.saveAll(study2Update);

        return study;
    }

    private Long getMaxStudyId() {
        Long maxStudyId = studyRepository.getMaxStudyId();
        if(maxStudyId == null) {
            maxStudyId = 0L;
        }
        return maxStudyId;
    }

    private Study findPrevStudy(Study study) {
        LocalDate localDate = LocalDate.now();
        String dk = study.getStudyKey().getDeviceKey();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Long prevStudyId = studyRepository.getMaxStudyIdByDkAndDate(dk, dateTimeFormatter.format(localDate));
        String prevLocalStudyId = dk + "|" + prevStudyId;
        Optional<Study> prevStudy = studyRepository.findByLocalStudyId(prevLocalStudyId);
        return prevStudy.isPresent() ? prevStudy.get() : null;
    }

}
