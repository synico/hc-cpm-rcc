package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.MRSerie;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.MRSerieRepository;
import cn.gehc.cpm.repository.StudyRepository;
import cn.gehc.cpm.util.DataUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service(value = "mrSeriePullJob")
public class MRSeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(MRSeriePullJob.class);

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private MRSerieRepository mrSerieRepository;

    public void insertData(@Headers Map<String, Object> headers, @Body Object body) {
        log.info("start to insert data to mr_serie");
        List<Map<String, Object>> dataMap = (List<Map<String, Object>>) body;
        Set<Study> studySet = new HashSet<>();
        Set<MRSerie> mrSerieSet = new HashSet<>();

        Map<String, TreeSet<MRSerie>> studyWithSerieMap = new HashMap<>();

        Long lastPolledValue = null;
        Study study;
        MRSerie mrSerie;
        // save study, mr_serie
        for(Map<String, Object> serieProps : dataMap) {
            log.debug(serieProps.toString());
            study = DataUtil.convertProps2Study(serieProps);
            studySet.add(study);

            mrSerie = DataUtil.convertProps2MRSerie(serieProps);
            mrSerieSet.add(mrSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            TreeSet<MRSerie> mrSerieList;
            if(studyWithSerieMap.get(study.getLocalStudyId()) == null) {
                mrSerieList = new TreeSet<>();
            } else {
                mrSerieList = studyWithSerieMap.get(study.getLocalStudyId());
            }
            mrSerieList.add(mrSerie);
            studyWithSerieMap.put(study.getLocalStudyId(), mrSerieList);

            if(lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }
        }

        if(mrSerieSet.size() > 0) {
            mrSerieRepository.saveAll(mrSerieSet);
        }

        //update study
        List<Study> studyList = studyRepository.findByLocalStudyIdIn(studyWithSerieMap.keySet());
        for(Study st : studySet) {
            if(!studyList.contains(st)) {
                studyList.add(st);
            }
        }
        List<Study> study2Update = new ArrayList<>(studyList.size());
        for(Study tmpStudy : studyList) {
            TreeSet<MRSerie> serieSet = studyWithSerieMap.get(tmpStudy.getLocalStudyId());
            if(serieSet != null && serieSet.size() > 0) {
                MRSerie lastMRSerie = serieSet.last();
                MRSerie firstMRSerie = serieSet.first();
                if(lastMRSerie == null || lastMRSerie.getAcquisitionDatetime() == null || lastMRSerie.getAcquisitionDuration() == null) {
                    return;
                }
                if(firstMRSerie == null || firstMRSerie.getAcquisitionDatetime() == null || firstMRSerie.getAcquisitionDuration() == null) {
                    return;
                }
                Date lastSerieDate = DataUtil.getLastSerieDate(serieSet.last());
                //update study start time
                if(tmpStudy.getStudyStartTime() == null) {
                    tmpStudy.setStudyStartTime(serieSet.first().getAcquisitionDatetime());
                } else {
                    if(tmpStudy.getStudyStartTime().compareTo(serieSet.first().getAcquisitionDatetime()) > 0) {
                        tmpStudy.setStudyStartTime(serieSet.first().getAcquisitionDatetime());
                    }
                }
                //update study end time
                if(tmpStudy.getStudyEndTime() == null) {
                    tmpStudy.setStudyEndTime(lastSerieDate);
                } else {
                    if(tmpStudy.getStudyEndTime().compareTo(lastSerieDate) < 0) {
                        tmpStudy.setStudyEndTime(lastSerieDate);
                    }
                }
                study2Update.add(tmpStudy);
            }
        }

        if(study2Update.size() > 0) {
            studyRepository.saveAll(study2Update);
        }

        if(lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }

}
