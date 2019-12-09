package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.domain.XASerie;
import cn.gehc.cpm.domain.XAStudy;
import cn.gehc.cpm.repository.StudyRepository;
import cn.gehc.cpm.repository.XASerieRepository;
import cn.gehc.cpm.repository.XAStudyRepository;
import cn.gehc.cpm.util.DataUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
  private XAStudyRepository xaStudyRepository;

  @Autowired
  private XASerieRepository xaSerieRepository;

  public void insertData(@Headers Map<String, Object> headers, @Body Object body) {
    log.info("start to insert data to xa_serie");
    List<Map<String, Object>> dataMap = (List<Map<String, Object>>) body;
    Set<Study> studySet = new HashSet<>();
    Set<XAStudy> xaStudySet = new HashSet<>();
    Set<XASerie> xaSerieSet = new HashSet<>();

    Map<String, TreeSet<XASerie>> studyWithSerieMap = new HashMap<>();

    Long lastPolledValue = null;
    Study study;
    XAStudy xaStudy;
    XASerie xaSerie;
    // save study, mr_serie
    for(Map<String, Object> serieProps : dataMap) {
      log.debug(serieProps.toString());
      study = DataUtil.convertProps2Study(serieProps);
      studySet.add(study);

      xaStudy = DataUtil.convertProps2XAStudy(serieProps);
      xaStudySet.add(xaStudy);

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

    if(xaStudySet.size() > 0) {
      xaStudyRepository.saveAll(xaStudySet);
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
        XASerie firstXASerie = null, lastXASerie = null;
        Iterator<XASerie> ascItr = serieSet.iterator();
        while(ascItr.hasNext()) {
          XASerie tmpSerie = ascItr.next();
          if(tmpSerie != null && tmpSerie.getSeriesDate() != null) {
            firstXASerie = tmpSerie;
            break;
          }
        }
        Iterator<XASerie> descItr = serieSet.descendingIterator();
        while(descItr.hasNext()) {
          XASerie tmpSerie = descItr.next();
          if(tmpSerie != null
            && tmpSerie.getSeriesDate() != null
            && tmpSerie.getExposureTime() != null) {
            lastXASerie = tmpSerie;
            break;
          }
        }

        //update study start time
        tmpStudy.setStudyStartTime(firstXASerie.getSeriesDate());
        //update study end time
        tmpStudy.setStudyEndTime(DataUtil.getLastSerieDate(lastXASerie));

        study2Update.add(tmpStudy);
      }
    }

    if(study2Update.size() > 0) {
      studyRepository.saveAll(study2Update);
    }

    // update prev_local_study_id and next_local_study_id
    Map<String, Set<String>> aetStudyDateMap = new HashMap<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    for(Study s : study2Update) {
      String aet = s.getStudyKey().getAet();
      Set studyDateSet = aetStudyDateMap.get(aet);
      if(studyDateSet == null) {
        studyDateSet = new HashSet();
      }
      studyDateSet.add(sdf.format(s.getStudyDate()));
      aetStudyDateMap.put(aet, studyDateSet);
    }
    for(Map.Entry<String, Set<String>> aetStudyDate : aetStudyDateMap.entrySet()) {
      String aet = aetStudyDate.getKey();
      Set<String> studyDateSet = aetStudyDate.getValue();
      for(String studyDateString : studyDateSet) {
        List<Study> studies = studyRepository.findByAETAndStudyDateChar(aet, studyDateString);
        Study prevStudy = (studies != null && studies.size() > 0) ? studies.get(0) : null;
        Study currentStudy = null;
        for(int idx = 1; idx < studies.size(); idx++) {
          currentStudy = studies.get(idx);
          prevStudy.setNextLocalStudyId(currentStudy.getLocalStudyId());
          currentStudy.setPrevLocalStudyId(prevStudy.getLocalStudyId());
          prevStudy = currentStudy;
        }
        studyRepository.saveAll(studies);
      }
    }

    if(lastPolledValue != null) {
      super.updateLastPullValue(headers, lastPolledValue.toString());
    }
  }

}
