package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.domain.StudyKey;
import cn.gehc.cpm.repository.MockStudyRepository;
import cn.gehc.cpm.utils.DeviceConstant;
import cn.gehc.cpm.utils.StudyConstant;
import cn.gehc.cpm.utils.StudyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static cn.gehc.cpm.utils.DeviceConstant.*;

@Component
public class StudyJob {


    @Autowired
    private MockStudyRepository studyRepository;

    @Autowired
    private CTStudyJob ctStudyJob;

    @Autowired
    private MRStudyJob mrStudyJob;

    public List<Study> generateStudies() {
        Long currentMaxStudyId = this.getMaxStudyId();
        List<DeviceConstant.AE> devices = DEVICE_LIST;
        List<Study> studyList = new ArrayList<>(devices.size());

        Long studyId = currentMaxStudyId;
        for(DeviceConstant.AE device : devices) {
            studyId++;
            Study study = new Study();
            StudyKey studyKey = new StudyKey();
            studyKey.setAet(device.getAet());
            studyKey.setId(studyId);
            study.setStudyKey(studyKey);
            study.setAccessionNumber(StudyUtils.generateAccessionNumber(device.name()));
            study.setDtLastUpdate(new Date());
            study.setHasRepeatedSeries(Boolean.FALSE);
            study.setLocalStudyId(device.getAet() + "|" + studyId);
            study.setPatientAge(StudyUtils.generatePatientAge());
            study.setPatientId(StudyUtils.generatePatientId());
            study.setPatientSex(StudyUtils.generatePatientSex());
            study.setPublished(1);

            Date studyDate = StudyUtils.generateStudyDate();
            study.setStudyDate(studyDate);
            study.setStudyStartTime(StudyUtils.generateStudyStartTime(studyDate.toInstant()));
            study.setStudyEndTime(StudyUtils.generateStudyEndTime(studyDate.toInstant()));

            switch (device.getType()) {
                case CT:
                    study.setDType(StudyConstant.Type.CTSTUDY.getName());
                    study.setModality(StudyConstant.MODALITY.CT.name());
                    study.setTargetRegionCount(StudyUtils.generateTargetRegionCount(StudyConstant.Type.CTSTUDY));
                    study.setStudyDescription(StudyUtils.generateStudyDesc(StudyConstant.Type.CTSTUDY));
                    ctStudyJob.generateCTStudies(study);
                    break;
                case MR:
                    study.setDType(StudyConstant.Type.MRSTUDY.getName());
                    study.setModality(StudyConstant.MODALITY.MR.name());
                    study.setTargetRegionCount(StudyUtils.generateTargetRegionCount(StudyConstant.Type.MRSTUDY));
                    study.setStudyDescription(StudyUtils.generateStudyDesc(StudyConstant.Type.MRSTUDY));
                    mrStudyJob.generateMRStudy(study);
                    break;
            }

            studyList.add(study);
        }

        Iterable result = studyRepository.saveAll(studyList);
        return studyList;
    }

    private Long getMaxStudyId() {
        Long maxStudyId = studyRepository.getMaxStudyId();
        if(maxStudyId == null) {
            maxStudyId = 0L;
        }
        return maxStudyId;
    }

}
