package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.*;
import cn.gehc.cpm.repository.XASerieRepository;
import cn.gehc.cpm.repository.XAStudyRepository;
import cn.gehc.cpm.util.DataUtil;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service(value = "xaSeriePullJob")
public class XASeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(XASeriePullJob.class);

    @Autowired
    private XAStudyRepository xaStudyRepository;

    @Autowired
    private XASerieRepository xaSerieRepository;

    @Override
    public void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to insert data to xa_serie, [ {} ] records will be processed", body.size());

        Set<Study> studySet = new HashSet<>();
        Set<XAStudy> xaStudySet = new HashSet<>();
        Set<XASerie> xaSerieSet = new HashSet<>();

        Map<String, TreeSet<XASerie>> studyWithSerieMap = new HashMap<>();

        Long lastPolledValue = null;
        Study study;
        XAStudy xaStudy;
        XASerie xaSerie;
        Long orgId = 0L;
        // save study, mr_serie
        for (Map<String, Object> serieProps : body) {
            log.debug(serieProps.toString());

            // retrieve org entity id by facility code
            String facilityCode = DataUtil.getStringFromProperties(serieProps, "facility_code");
            if (orgId.longValue() == 0 && StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                if (orgEntityList.size() > 0) {
                    orgId = orgEntityList.get(0).getOrgId();
                    log.info("facility [ {} ] is retrieved", orgId);
                } else {
                    // !!! IMPORTANT !!! job will not save data to database while org_entity has not been set
                    log.warn("The org/device has not been synchronized, job will not save data");
                    return;
                }
            }
            if (orgId.longValue() == 0 && StringUtils.isBlank(facilityCode)) {
                log.error("facility hasn't been configured for aet: [ {} ]", DataUtil.getStringFromProperties(serieProps, "aet"));
                continue;
            }
            serieProps.put("org_id", orgId);

            study = DataUtil.convertProps2Study(serieProps);
            studySet.add(study);

            xaStudy = DataUtil.convertProps2XAStudy(serieProps);
            xaStudySet.add(xaStudy);

            xaSerie = DataUtil.convertProps2XASerie(serieProps);
            xaSerieSet.add(xaSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            TreeSet<XASerie> xaSerieList;
            if (studyWithSerieMap.get(study.getLocalStudyId()) == null) {
                xaSerieList = new TreeSet<>();
            } else {
                xaSerieList = studyWithSerieMap.get(study.getLocalStudyId());
            }
            xaSerieList.add(xaSerie);
            studyWithSerieMap.put(study.getLocalStudyId(), xaSerieList);

            if (lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }
        }

        if (xaStudySet.size() > 0) {
            xaStudyRepository.saveAll(xaStudySet);
        }

        if (xaSerieSet.size() > 0) {
            xaSerieRepository.saveAll(xaSerieSet);
        }

        //update study
        List<Study> studyList = studyRepository.findByLocalStudyIdIn(studyWithSerieMap.keySet());
        for (Study st : studySet) {
            if (!studyList.contains(st)) {
                studyList.add(st);
            }
        }
        List<Study> study2Update = new ArrayList<>(studyList.size());
        List<XAStudy> xaStudy2Update = new ArrayList<>(studyList.size());
        for (Study tmpStudy : studyList) {
            TreeSet<XASerie> serieSet = studyWithSerieMap.get(tmpStudy.getLocalStudyId());
            if (serieSet != null && serieSet.size() > 0) {
                XASerie firstXASerie = null, lastXASerie = null;
                Iterator<XASerie> ascItr = serieSet.iterator();
                while (ascItr.hasNext()) {
                    XASerie tmpSerie = ascItr.next();
                    if (tmpSerie != null && tmpSerie.getSeriesDate() != null) {
                        firstXASerie = tmpSerie;
                        break;
                    }
                }
                Iterator<XASerie> descItr = serieSet.descendingIterator();
                while (descItr.hasNext()) {
                    XASerie tmpSerie = descItr.next();
                    if (tmpSerie != null
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

                //update protocol_key and protocol_name by first serie of XA study
                XAStudy tmpXAStudy = xaStudySet.stream()
                        .filter(xa -> tmpStudy.getLocalStudyId().equals(xa.getLocalStudyId()))
                        .findFirst().get();
                tmpXAStudy.setProtocolKey(firstXASerie.getProtocolKey());
                tmpXAStudy.setProtocolName(firstXASerie.getProtocolName());
                xaStudy2Update.add(tmpXAStudy);
            }
        }

        if (xaStudy2Update.size() > 0) {
            xaStudyRepository.saveAll(xaStudy2Update);
        }

        if (study2Update.size() > 0) {
            studyRepository.saveAll(study2Update);
        }

        log.info("[ {} ] studies have been saved, [ {} ] xa studies have been saved, [ {} ] xa series have been saved",
                study2Update.size(), xaStudySet.size(), xaSerieSet.size());

        linkStudies(study2Update);

        if (lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }

}
