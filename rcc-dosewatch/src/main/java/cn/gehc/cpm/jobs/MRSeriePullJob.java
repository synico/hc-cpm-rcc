package cn.gehc.cpm.jobs;

import cn.gehc.cpm.domain.MRSerie;
import cn.gehc.cpm.domain.MRStudy;
import cn.gehc.cpm.domain.OrgEntity;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.MRSerieRepository;
import cn.gehc.cpm.repository.MRStudyRepository;
import cn.gehc.cpm.util.DataUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "mrSeriePullJob")
public class MRSeriePullJob extends TimerDBReadJob {

    private static final Logger log = LoggerFactory.getLogger(MRSeriePullJob.class);

    @Autowired
    private MRStudyRepository mrStudyRepository;

    @Autowired
    private MRSerieRepository mrSerieRepository;

    public void insertData(@Headers Map<String, Object> headers, @Body List<Map<String, Object>> body) {
        log.info("start to insert data to mr_serie");

        Set<Study> studiesFromJob = new HashSet<>();
        Set<MRStudy> mrStudySet = new HashSet<>();
        Set<MRSerie> mrSerieSet = new HashSet<>();

        Map<String, TreeSet<MRSerie>> studyWithSeriesMap = new HashMap<>();

        Long lastPolledValue = null;
        Study study;
        MRStudy mrStudy;
        MRSerie mrSerie;
        Long orgId = 0L;
        // save study, mr_serie
        for(Map<String, Object> serieProps : body) {
            log.debug(serieProps.toString());

            // retrieve org entity id by facility code
            String facilityCode = DataUtil.getStringFromProperties(serieProps, "facility_code");
            if(orgId.longValue() == 0 && StringUtils.isNotBlank(facilityCode)) {
                List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName(facilityCode);
                if(orgEntityList.size() > 0) {
                    orgId = orgEntityList.get(0).getOrgId();
                    log.info("facility {} is retrieved", orgId);
                } else {
                    // !!! IMPORTANT !!! job will not save data to database while org_entity has not been set
                    log.warn("The org/device has not been synchronized, job will not save data");
                    return;
                }
            }
            if(orgId.longValue() == 0 && StringUtils.isBlank(facilityCode)) {
                log.error("facility hasn't been configured for aet: {}", DataUtil.getStringFromProperties(serieProps, "aet"));
                continue;
            }
            serieProps.put("org_id", orgId);

            study = DataUtil.convertProps2Study(serieProps);
            studiesFromJob.add(study);

            mrStudy = DataUtil.convertProps2MRStudy(serieProps);
            mrStudySet.add(mrStudy);

            mrSerie = DataUtil.convertProps2MRSerie(serieProps);
            mrSerieSet.add(mrSerie);

            Long jointKey = DataUtil.getLongFromProperties(serieProps, "joint_key");

            if(lastPolledValue == null) {
                lastPolledValue = jointKey;
            } else {
                lastPolledValue = lastPolledValue > jointKey ? lastPolledValue : jointKey;
            }
        }

        if(mrStudySet.size() > 0) {
            mrStudyRepository.saveAll(mrStudySet);
        }

        if(mrSerieSet.size() > 0) {
            mrSerieRepository.saveAll(mrSerieSet);
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
        List<MRSerie> mrSeriesFromDB = mrSerieRepository.findByLocalStudyKeyIn(studyIds);
        for(MRSerie mrse : mrSeriesFromDB) {
            TreeSet<MRSerie> mrSeries = studyWithSeriesMap.get(mrse.getLocalStudyKey());
            if(mrSeries == null) {
                mrSeries = new TreeSet<>();
            }
            mrSeries.add(mrse);
            studyWithSeriesMap.put(mrse.getLocalStudyKey(), mrSeries);
        }

        List<Study> study2Update = new ArrayList<>(studyList.size());
        // MR study and Study is One-To-One relationship
        List<MRStudy> mrStudy2Update = new ArrayList<>(studyList.size());
        for(Study tmpStudy : studyList) {
            TreeSet<MRSerie> serieSet = studyWithSeriesMap.get(tmpStudy.getLocalStudyId());
            if(serieSet != null && serieSet.size() > 0) {
                MRSerie firstMRSerie = null, lastMRSerie = null;
                Iterator<MRSerie> ascItr = serieSet.iterator();
                while (ascItr.hasNext()) {
                    MRSerie tmpSerie = ascItr.next();
                    if(tmpSerie != null && tmpSerie.getAcquisitionDatetime() != null) {
                        firstMRSerie = tmpSerie;
                        break;
                    }
                }
                Iterator<MRSerie> descItr = serieSet.descendingIterator();
                while(descItr.hasNext()) {
                    MRSerie tmpSerie = descItr.next();
                    if(tmpSerie != null
                        && tmpSerie.getAcquisitionDatetime() != null
                        && tmpSerie.getAcquisitionDuration() != null) {
                        lastMRSerie = tmpSerie;
                        break;
                    }
                }

                //update study start time
                tmpStudy.setStudyStartTime(firstMRSerie.getAcquisitionDatetime());
                //update study end time
                tmpStudy.setStudyEndTime(DataUtil.getLastSerieDate(lastMRSerie));
                study2Update.add(tmpStudy);

                //update protocol_key and protocol_name by first serie of MR study
                MRStudy tmpMRStudy = mrStudySet.stream()
                        .filter(mr -> tmpStudy.getLocalStudyId().equals(mr.getLocalStudyId()))
                        .findFirst().get();
                tmpMRStudy.setProtocolKey(firstMRSerie.getProtocolKey());
                tmpMRStudy.setProtocolName(firstMRSerie.getProtocolName());
                mrStudy2Update.add(tmpMRStudy);

                //to calculate protocol by serie
                Long targetRegionCount = serieSet.stream().map(mrse -> mrse.getProtocolName())
                        .filter(protocolName -> StringUtils.isNotBlank(protocolName))
                        .distinct()
                        .count();
                if(targetRegionCount > 1) {
                    log.debug("***************************************************************");
                    log.debug("study: {}, target_region: {}, target region count: {}",
                            tmpStudy.getLocalStudyId(),
                            serieSet.stream().map(mrse -> mrse.getProtocolName()).distinct().reduce((x, y) -> x + "," + y).get(),
                            targetRegionCount);
                    log.debug("***************************************************************");
                }
                tmpStudy.setTargetRegionCount(targetRegionCount.intValue());

                //mark repeated series
                Set<MRSerie> filteredSeries = serieSet.stream()
                    .filter(serie -> serie.getStartSliceLocation() !=null)
                    .filter(serie -> serie.getEndSliceLocation() != null)
                    .collect(Collectors.toSet());
                Boolean hasRepeatedSeries = this.hasRepeatedSeries(filteredSeries);
                tmpStudy.setHasRepeatedSeries(hasRepeatedSeries);

                study2Update.add(tmpStudy);
            }
        }

        if(mrStudy2Update.size() > 0) {
            mrStudyRepository.saveAll(mrStudy2Update);
        }

        if(study2Update.size() > 0) {
            studyRepository.saveAll(study2Update);
        }

        linkStudies(study2Update);

        if(lastPolledValue != null) {
            super.updateLastPullValue(headers, lastPolledValue.toString());
        }
    }

    /**
     * For MR series, we need to group series by series description, then check series
     * have same series description and scan range is overlapping.
     * @param mrSeries
     * @return Boolean
     */
    private Boolean hasRepeatedSeries(Set<MRSerie> mrSeries) {
        Boolean hasRepeatedSeries = Boolean.FALSE;
        Map<String, List<MRSerie>> seriesByType = new HashMap<>();
        mrSeries.stream().forEach(serie -> {
            List<MRSerie> serieList = seriesByType.get(serie.getSeriesDescription());
            if(serieList == null) {
                serieList = new ArrayList<>();
            }
            serieList.add(serie);
            seriesByType.put(serie.getSeriesDescription(), serieList);
        });

        for(Map.Entry<String, List<MRSerie>> seriesEntry : seriesByType.entrySet()) {
            List<MRSerie> mrSerieList = seriesEntry.getValue();
            List<MRSerie> series2Compare;
            Set<MRSerie> series2Update = new HashSet<>();
            if(mrSerieList.size() > 1) {
                for(MRSerie baseSerie : mrSerieList) {
                    series2Compare = mrSerieList.stream()
                        .filter(serie -> baseSerie.getLocalSerieId() != serie.getLocalSerieId())
                        .collect(Collectors.toList());
                    for(MRSerie mrSerie : series2Compare) {
                        //start slice location
                        if(mrSerie.getStartSliceLocation() > baseSerie.getStartSliceLocation()
                            && mrSerie.getStartSliceLocation() < baseSerie.getEndSliceLocation()) {
                            log.info("study {} has repeated series", mrSerie.getLocalStudyKey());
                            if(log.isDebugEnabled()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    log.debug("base serie: {}", objectMapper.writeValueAsString(baseSerie));
                                    log.debug("current serie: {}", objectMapper.writeValueAsString(mrSerie));
                                } catch(Exception ex) {
                                }
                            }
                            baseSerie.setIsRepeated(Boolean.TRUE);
                            mrSerie.setIsRepeated(Boolean.TRUE);
                            series2Update.add(baseSerie);
                            series2Update.add(mrSerie);
                            hasRepeatedSeries = Boolean.TRUE;
                        }
                        //end slice location
                        if(mrSerie.getEndSliceLocation() > baseSerie.getStartSliceLocation()
                            && mrSerie.getEndSliceLocation() < baseSerie.getEndSliceLocation()) {
                            log.info("study {} has repeated series", mrSerie.getLocalStudyKey());
                            if(log.isDebugEnabled()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                try {
                                    log.debug("base serie: {}", objectMapper.writeValueAsString(baseSerie));
                                    log.debug("current serie: {}", objectMapper.writeValueAsString(mrSerie));
                                } catch(Exception ex) {
                                }
                            }
                            baseSerie.setIsRepeated(Boolean.TRUE);
                            mrSerie.setIsRepeated(Boolean.TRUE);
                            series2Update.add(baseSerie);
                            series2Update.add(mrSerie);
                            hasRepeatedSeries = Boolean.TRUE;
                        }
                    }
                }
            }
            if(series2Update.size() > 1) {
                mrSerieRepository.saveAll(series2Update);
            }
        }
        return hasRepeatedSeries;
    }

}
