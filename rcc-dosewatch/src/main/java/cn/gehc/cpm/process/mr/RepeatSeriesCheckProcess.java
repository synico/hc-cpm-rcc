package cn.gehc.cpm.process.mr;

import cn.gehc.cpm.domain.MRSerie;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.process.StudyPostProcess;
import cn.gehc.cpm.repository.MRSerieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This process is used for updating fields:
 * study.has_repeated_series, mr_serie.is_repeated
 *
 * @author 212706300
 */

@Component("mrRepeatSeriesCheckProcess")
public class RepeatSeriesCheckProcess implements StudyPostProcess<MRSerie> {

    private static final Logger log = LoggerFactory.getLogger(RepeatSeriesCheckProcess.class);

    private Integer priority;

    @Autowired
    private MRSerieRepository mrSerieRepository;

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
    * For MR series, we need to group series by series description, then check series * have same
    * series description and scan range is overlapping.
    *
    * @param studyList
    * @param studyWithSeriesMap
    */
    @Override
    public void process(Collection<Study> studyList, Map<String, TreeSet<MRSerie>> studyWithSeriesMap) {
        log.info("start to process studies if study has repeated series, priority of process: {}, num of studies: {}",
                this.priority, studyList.size());

        for (Study study : studyList) {
            TreeSet<MRSerie> serieSet = studyWithSeriesMap.get(study.getLocalStudyId());
            // exclude series without slice location
            Set<MRSerie> filteredSeries = serieSet.stream()
                    .filter(serie -> serie.getStartSliceLocation() != null)
                    .filter(serie -> serie.getEndSliceLocation() != null)
                    .collect(Collectors.toSet());
            Boolean hasRepeatedSeries = Boolean.FALSE;
            if(!filteredSeries.isEmpty()) {
                Map<String, List<MRSerie>> seriesByType = new HashMap<>(filteredSeries.size());
                filteredSeries.stream().forEach(serie -> {
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
                    if(!mrSerieList.isEmpty()) {
                        for(MRSerie baseSerie : mrSerieList) {
                            series2Compare = mrSerieList.stream()
                                    .filter(serie -> baseSerie.getLocalSerieId() != serie.getLocalSerieId())
                                    .collect(Collectors.toList());
                            for(MRSerie mrSerie : series2Compare) {
                                //start slice location
                                if(mrSerie.getStartSliceLocation() > baseSerie.getStartSliceLocation()
                                        && mrSerie.getStartSliceLocation() < baseSerie.getEndSliceLocation()) {
                                    log.info("study {} has repeated series", mrSerie.getLocalStudyKey());
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
                                    baseSerie.setIsRepeated(Boolean.TRUE);
                                    mrSerie.setIsRepeated(Boolean.TRUE);
                                    series2Update.add(baseSerie);
                                    series2Update.add(mrSerie);
                                    hasRepeatedSeries = Boolean.TRUE;
                                }
                            }
                        }
                    }
                    if(!series2Update.isEmpty()) {
                        mrSerieRepository.saveAll(series2Update);
                    }
                }
            }
            study.setHasRepeatedSeries(hasRepeatedSeries);
        }

        log.info("end of process to check if study has repeated series");
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }
}
