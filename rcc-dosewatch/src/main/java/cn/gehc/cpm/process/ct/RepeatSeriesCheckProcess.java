package cn.gehc.cpm.process.ct;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.process.StudyPostProcess;
import cn.gehc.cpm.repository.CTSerieRepository;
import cn.gehc.cpm.util.SerieType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This process is used for updating fields:
 * study.has_repeated_series, ct_serie.is_repeated
 *
 * @author 212706300
 */

@Component("ctRepeatSeriesCheckProcess")
public class RepeatSeriesCheckProcess implements StudyPostProcess<Study, CTSerie> {

    private static final Logger log = LoggerFactory.getLogger(RepeatSeriesCheckProcess.class);

    private Integer priority;

    @Autowired
    private CTSerieRepository ctSerieRepository;

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
    * For CT series, we need to group series by type(dtype), then check the scan range to count if
    * there are repeated series in the study
    *
    * @param studyList
    * @param studyWithSeriesMap
    */
    @Override
    public void process(Collection<Study> studyList, Map<String, Set<CTSerie>> studyWithSeriesMap) {
        log.info("start to process studies if study has repeated series, priority of process: {}, num of studies: {}",
                this.priority, studyList.size());

        for (Study study : studyList) {
            Set<CTSerie> serieSet = studyWithSeriesMap.get(study.getLocalStudyId());
            // exclude series without slice location
            Set<CTSerie> filteredSeries = serieSet.stream()
                  .filter(serie -> serie.getStartSliceLocation() != null)
                  .filter(serie -> serie.getEndSliceLocation() != null)
                  .collect(Collectors.toSet());
            Boolean hasRepeatedSeries = Boolean.FALSE;
            if (!filteredSeries.isEmpty()) {
                Map<String, List<CTSerie>> seriesByType = new HashMap<>(filteredSeries.size());
                filteredSeries.stream().filter(serie -> !SerieType.CONSTANT_ANGLE.getType().equals(serie.getDType()))
                    .forEach(serie -> {
                        List<CTSerie> serieList = seriesByType.get(serie.getDType());
                        if(serieList == null) {
                            serieList = new ArrayList<>();
                        }
                        serieList.add(serie);
                        seriesByType.put(serie.getDType(), serieList);
                    });

                for (Map.Entry<String, List<CTSerie>> seriesEntry : seriesByType.entrySet()) {
                    List<CTSerie> ctSerieList = seriesEntry.getValue();
                    List<CTSerie> series2Compare;
                    Set<CTSerie> series2Update = new HashSet<>();
                    if (!ctSerieList.isEmpty()) {
                        for (CTSerie baseSerie : ctSerieList) {
                            series2Compare = ctSerieList.stream()
                                    .filter(serie -> baseSerie.getLocalSerieId() != serie.getLocalSerieId())
                                    .collect(Collectors.toList());
                            for (CTSerie ctSerie : series2Compare) {
                                //start slice location check
                                if (ctSerie.getStartSliceLocation() > baseSerie.getStartSliceLocation()
                                        && ctSerie.getStartSliceLocation() < baseSerie.getEndSliceLocation()) {
                                    log.info("study {} has repeated series", ctSerie.getLocalStudyKey());
                                    baseSerie.setIsRepeated(Boolean.TRUE);
                                    ctSerie.setIsRepeated(Boolean.TRUE);
                                    series2Update.add(baseSerie);
                                    series2Update.add(ctSerie);
                                    hasRepeatedSeries = Boolean.TRUE;
                                }
                                //end slice location check
                                if (ctSerie.getEndSliceLocation() > baseSerie.getStartSliceLocation()
                                        && ctSerie.getEndSliceLocation() < baseSerie.getEndSliceLocation()) {
                                    log.info("study {} has repeated series", ctSerie.getLocalStudyKey());
                                    baseSerie.setIsRepeated(Boolean.TRUE);
                                    ctSerie.setIsRepeated(Boolean.TRUE);
                                    series2Update.add(baseSerie);
                                    series2Update.add(ctSerie);
                                    hasRepeatedSeries = Boolean.TRUE;
                                }
                            }
                        }
                    }
                    if (!series2Update.isEmpty()) {
                        ctSerieRepository.saveAll(series2Update);
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
