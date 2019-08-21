package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.CTStudy;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.CTSerieRepository;
import cn.gehc.cpm.repository.CTStudyRepository;
import cn.gehc.cpm.repository.StudyRepository;
import cn.gehc.cpm.util.DataUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service(value = "ctSeriePullJob")
public class CTSeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(CTSeriePullJob.class);

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private CTStudyRepository ctStudyRepository;

    @Autowired
    private CTSerieRepository ctSerieRepository;

    public void insertData(@Headers Map<String, Object> headers, @Body Object body) {
        log.info("start to insert/update data to ct_serie");
        List<Map<String, Object>> dataMap = (List<Map<String, Object>>) body;

        Set<Study> studySet = new HashSet<>();
        Set<CTStudy> ctStudySet = new HashSet<>();
        Set<CTSerie> ctSerieSet = new HashSet<>();

        Map<String, TreeSet<CTSerie>> studyWithSerieMap = new HashMap<>();

        Long lastPolledValue = null;
        Study study;
        CTStudy ctStudy;
        CTSerie ctSerie;
        // save study, ct_study, ct_serie
        for(Map<String, Object> serieProps : dataMap) {
            log.debug(serieProps.toString());
            study = DataUtil.convertProps2Study(serieProps);
            studySet.add(study);

            ctStudy = DataUtil.convertProps2CTStudy(serieProps);
            ctStudySet.add(ctStudy);

            ctSerie = DataUtil.convertProps2CTSerie(serieProps);
            ctSerieSet.add(ctSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            TreeSet<CTSerie> ctSerieList;
            if(studyWithSerieMap.get(study.getLocalStudyId()) == null) {
                ctSerieList = new TreeSet();
            } else {
                ctSerieList = studyWithSerieMap.get(study.getLocalStudyId());
            }
            ctSerieList.add(ctSerie);
            studyWithSerieMap.put(study.getLocalStudyId(), ctSerieList);


            if(lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }
        }

        if(studySet.size() > 0) {
            studyRepository.saveAll(studySet);
        }

        if(ctSerieSet.size() > 0) {
            ctStudyRepository.saveAll(ctStudySet);
        }

        if(ctSerieSet.size() > 0) {
            ctSerieRepository.saveAll(ctSerieSet);
        }

        //update study
        List<Study> studyList = studyRepository.findByLocalStudyIdIn(studyWithSerieMap.keySet());
        List<Study> study2Update = new ArrayList<>(studyList.size());
        for(Study tmpStudy : studyList) {
            TreeSet<CTSerie> serieSet = studyWithSerieMap.get(tmpStudy.getLocalStudyId());
            Date lastSerieDate = DataUtil.getLastSerieDate(serieSet.last());

            //update study start time
            if(tmpStudy.getStudyStartTime() == null) {
                tmpStudy.setStudyStartTime(serieSet.first().getSeriesDate());
            } else {
                if(tmpStudy.getStudyStartTime().compareTo(serieSet.first().getSeriesDate()) > 0) {
                    tmpStudy.setStudyStartTime(serieSet.first().getSeriesDate());
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

        if(study2Update.size() > 0) {
            studyRepository.saveAll(study2Update);
        }

        if(lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }
}
