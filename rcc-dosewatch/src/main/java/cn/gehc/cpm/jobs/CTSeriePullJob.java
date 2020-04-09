package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.CTStudy;
import cn.gehc.cpm.domain.OrgEntity;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.CTSerieRepository;
import cn.gehc.cpm.repository.CTStudyRepository;
import cn.gehc.cpm.util.DataUtil;
import cn.gehc.cpm.util.SerieType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service(value = "ctSeriePullJob")
public class CTSeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(CTSeriePullJob.class);

    @Autowired
    private CTStudyRepository ctStudyRepository;

    @Autowired
    private CTSerieRepository ctSerieRepository;

    public void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to insert/update data to ct_serie, [ {} ] records will be processed", body.size());

        Set<Study> studiesFromJob = new HashSet<>();
        Set<CTStudy> ctStudySet = new HashSet<>();
        Set<CTSerie> ctSerieSet = new HashSet<>();

        Map<String, TreeSet<CTSerie>> studyWithSeriesMap = new HashMap<>();

        Long lastPolledValue = null;
        Study study;
        CTStudy ctStudy;
        CTSerie ctSerie;
        Long orgId = 0L;
        // save study, ct_study, ct_serie
        for(Map<String, Object> serieProps : body) {
            log.debug(serieProps.toString());

            // retrieve org entity id by facility code
            String facilityCode = DataUtil.getStringFromProperties(serieProps, "facility_code");
            if(orgId.longValue() == 0 && StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                if(orgEntityList.size() > 0) {
                    orgId = orgEntityList.get(0).getOrgId();
                    log.info("facility < {} > is retrieved", orgId);
                } else {
                    // !!! IMPORTANT !!! job will not save data to database while org_entity has not been set
                    log.warn("The org/device has not been synchronized, job will not save data");
                    return;
                }
            }
            if(orgId.longValue() == 0 && StringUtils.isBlank(facilityCode)) {
                log.error("facility hasn't been configured for aet: < {} >", DataUtil.getStringFromProperties(serieProps, "aet"));
                continue;
            }
            serieProps.put("org_id", orgId);

            study = DataUtil.convertProps2Study(serieProps);
            studiesFromJob.add(study);

            ctStudy = DataUtil.convertProps2CTStudy(serieProps);
            ctStudySet.add(ctStudy);

            ctSerie = DataUtil.convertProps2CTSerie(serieProps);
            ctSerieSet.add(ctSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            if(lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }
        }

        if(ctStudySet.size() > 0) {
            ctStudyRepository.saveAll(ctStudySet);
        }

        if(ctSerieSet.size() > 0) {
            ctSerieRepository.saveAll(ctSerieSet);
        }

        List<String> studyIds = studiesFromJob.stream().map(s -> s.getLocalStudyId()).collect(Collectors.toList());
        //combine studies from job(hasn't been persisted) with studies from database
        List<Study> studyList = studyRepository.findByLocalStudyIdIn(studyIds);
        for(Study st : studiesFromJob) {
            if(!studyList.contains(st)) {
                studyList.add(st);
            }
        }
        //retrieve ct serie from database
        List<CTSerie> ctSeriesFromDB = ctSerieRepository.findByLocalStudyKeyIn(studyIds);
        for(CTSerie ctse : ctSeriesFromDB) {
            TreeSet<CTSerie> ctSeries = studyWithSeriesMap.get(ctse.getLocalStudyKey());
            if(ctSeries == null) {
                ctSeries = new TreeSet<>();
            }
            ctSeries.add(ctse);
            studyWithSeriesMap.put(ctse.getLocalStudyKey(), ctSeries);
        }

        List<Study> study2Update = new ArrayList<>(studyList.size());
        for(Study tmpStudy : studyList) {
            TreeSet<CTSerie> serieSet = studyWithSeriesMap.get(tmpStudy.getLocalStudyId());
            if(serieSet != null && serieSet.size() > 0) {
                CTSerie firstCTSerie = null, lastCTSerie = null;
                Iterator<CTSerie> ascItr = serieSet.iterator();
                while(ascItr.hasNext()) {
                    CTSerie tmpSerie = ascItr.next();
                    if(tmpSerie != null && tmpSerie.getSeriesDate() != null) {
                        firstCTSerie = tmpSerie;
                        break;
                    }
                }
                Iterator<CTSerie> descItr = serieSet.descendingIterator();
                while (descItr.hasNext()) {
                    CTSerie tmpSerie = descItr.next();
                    if(tmpSerie != null
                        && tmpSerie.getSeriesDate() != null
                        && tmpSerie.getExposureTime() != null) {
                        lastCTSerie = tmpSerie;
                        break;
                    }
                }

                //update study start time
                tmpStudy.setStudyStartTime(firstCTSerie.getSeriesDate());
                //update study end time
                tmpStudy.setStudyEndTime(DataUtil.getLastSerieDate(lastCTSerie));

                //to calculate target region count by serie
                Long targetRegionCount = serieSet.stream()
                        .filter(serie -> StringUtils.isNotBlank(serie.getTargetRegion()))
                        .filter(serie -> !SerieType.CONSTANT_ANGLE.getType().equals(serie.getDType()))
                        .map(serie -> serie.getTargetRegion())
                        .distinct()
                        .count();
                if(targetRegionCount > 1) {
                    log.debug("***************************************************************");
                    log.debug("study: {}, target_region: {}, target region count: {}",
                            tmpStudy.getLocalStudyId(),
                            serieSet.stream().map(se -> se.getTargetRegion()).distinct().reduce((x, y) -> x + "," + y).get(),
                            targetRegionCount);
                    log.debug("***************************************************************");
                }
                tmpStudy.setTargetRegionCount(targetRegionCount.intValue());
                log.debug("study: {}, target region count: {}", tmpStudy.getLocalStudyId(), tmpStudy.getTargetRegionCount());

                //mark repeated series
                Set<CTSerie> filteredSeries = serieSet.stream()
                    .filter(serie -> serie.getStartSliceLocation() !=null)
                    .filter(serie -> serie.getEndSliceLocation() != null)
                    .collect(Collectors.toSet());
                Boolean hasRepeatedSeries = this.hasRepeatedSeries(filteredSeries);
                tmpStudy.setHasRepeatedSeries(hasRepeatedSeries);

                study2Update.add(tmpStudy);
            }
        }

        if(study2Update.size() > 0) {
            studyRepository.saveAll(study2Update);
        }

        log.info("[ {} ] studies have been saved, [ {} ] ct studies have been saved, [ {} ] ct series have been saved",
                study2Update.size(), ctStudySet.size(), ctSerieSet.size());

        linkStudies(study2Update);

        if(lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }

    /**
     * For CT series, we need to group series by type(dtype), then check the scan range
     * to define if there are repeated series in the study
     * @param ctSeries
     * @return Boolean
     */
    private Boolean hasRepeatedSeries(Set<CTSerie> ctSeries) {
        Boolean hasRepeatedSeries = Boolean.FALSE;
        Map<String, List<CTSerie>> seriesByType = new HashMap<>();
        ctSeries.stream().filter(serie -> !SerieType.CONSTANT_ANGLE.getType().equals(serie.getDType()))
            .forEach(serie -> {
                List<CTSerie> serieList = seriesByType.get(serie.getDType());
                if(serieList == null) {
                    serieList = new ArrayList<>();
                }
                serieList.add(serie);
                seriesByType.put(serie.getDType(), serieList);
            });

        for(Map.Entry<String, List<CTSerie>> seriesEntry : seriesByType.entrySet()) {
            List<CTSerie> ctSerieList = seriesEntry.getValue();
            List<CTSerie> series2Compare;
            Set<CTSerie> series2Update = new HashSet<>();
            if(ctSerieList.size() > 1) {
                for(CTSerie baseSerie : ctSerieList) {
                    series2Compare = ctSerieList.stream()
                        .filter(serie -> baseSerie.getLocalSerieId() != serie.getLocalSerieId())
                        .collect(Collectors.toList());
                    for(CTSerie ctSerie : series2Compare) {
                        //start slice location
                        if(ctSerie.getStartSliceLocation() > baseSerie.getStartSliceLocation()
                            && ctSerie.getStartSliceLocation() < baseSerie.getEndSliceLocation()) {
                            log.info("study {} has repeated series", ctSerie.getLocalStudyKey());
                            if(log.isDebugEnabled()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    log.debug("base serie: {}", objectMapper.writeValueAsString(baseSerie));
                                    log.debug("current serie: {}", objectMapper.writeValueAsString(ctSerie));
                                } catch(Exception ex) {
                                }
                            }
                            baseSerie.setIsRepeated(Boolean.TRUE);
                            ctSerie.setIsRepeated(Boolean.TRUE);
                            series2Update.add(baseSerie);
                            series2Update.add(ctSerie);
                            hasRepeatedSeries = Boolean.TRUE;
                        }
                        //end slice location
                        if(ctSerie.getEndSliceLocation() > baseSerie.getStartSliceLocation()
                            && ctSerie.getEndSliceLocation() < baseSerie.getEndSliceLocation()) {
                            log.info("study {} has repeated series", ctSerie.getLocalStudyKey());
                            if(log.isDebugEnabled()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    log.debug("base serie: {}", objectMapper.writeValueAsString(baseSerie));
                                    log.debug("current serie: {}", objectMapper.writeValueAsString(ctSerie));
                                } catch(Exception ex) {
                                }
                            }
                            baseSerie.setIsRepeated(Boolean.TRUE);
                            ctSerie.setIsRepeated(Boolean.TRUE);
                            series2Update.add(baseSerie);
                            series2Update.add(ctSerie);
                            hasRepeatedSeries = Boolean.TRUE;
                        }
                    }
                }
            }
            if(series2Update.size() > 1) {
                ctSerieRepository.saveAll(series2Update);
            }
        }
        return hasRepeatedSeries;
    }
}
