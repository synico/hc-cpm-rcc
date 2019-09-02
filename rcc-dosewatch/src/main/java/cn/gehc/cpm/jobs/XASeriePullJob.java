package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.domain.XASerie;
import cn.gehc.cpm.repository.StudyRepository;
import cn.gehc.cpm.repository.XASerieRepository;
import cn.gehc.cpm.util.DataUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "xaSeriePullJob")
public class XASeriePullJob extends TimerDBReadJob {

  private static final Logger log = LoggerFactory.getLogger(XASeriePullJob.class);

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private XASerieRepository xaSerieRepository;

  public void insertData(@Headers Map<String, Object> headers, @Body Object body) {
    log.info("start to insert data to mr_serie");
    List<Map<String, Object>> dataMap = (List<Map<String, Object>>) body;
    Set<Study> studySet = new HashSet<>();
    Set<XASerie> xaSerieSet = new HashSet<>();

    Map<String, TreeSet<XASerie>> studyWithSerieMap = new HashMap<>();

    Long lastPolledValue = null;
    Study study;
    XASerie xaSerie;
    // save study, mr_serie
    for(Map<String, Object> serieProps : dataMap) {
      log.debug(serieProps.toString());
      study = DataUtil.convertProps2Study(serieProps);
      studySet.add(study);

      xaSerie = DataUtil.convertProps2XASerie(serieProps);
      xaSerieSet.add(xaSerie);

      Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

      TreeSet<XASerie> xaSerieList;
      if(studyWithSerieMap.get(study.getLocalStudyId()) == null) {
        xaSerieList = new TreeSet<>();
      } else {
        xaSerieList = studyWithSerieMap.get(study.getLocalStudyId());
      }
      xaSerieList.add(xaSerie);
      studyWithSerieMap.put(study.getLocalStudyId(), xaSerieList);

      if(lastPolledValue == null) {
        lastPolledValue = jointKey;
      } else {
        lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
      }
    }

    if(xaSerieSet.size() > 0) {
      xaSerieRepository.saveAll(xaSerieSet);
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
      TreeSet<XASerie> serieSet = studyWithSerieMap.get(tmpStudy.getLocalStudyId());
      if(serieSet != null && serieSet.size() > 0) {
        XASerie lastXASerie = serieSet.last();
        XASerie firstXASerie = serieSet.first();
        if(lastXASerie == null || lastXASerie.getSeriesDate() == null || lastXASerie.getExposureTime() == null) {
          return;
        }
        if(firstXASerie == null || firstXASerie.getSeriesDate() == null || firstXASerie.getExposureTime() == null) {
          return;
        }
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
    }

    if(study2Update.size() > 0) {
      studyRepository.saveAll(study2Update);
    }

    if(lastPolledValue != null) {
      super.updateLastPullValue(headers, lastPolledValue.toString());
    }
  }

}
