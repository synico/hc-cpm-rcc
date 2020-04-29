package cn.gehc.cpm.process.ct;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.process.StudyPostProcess;
import cn.gehc.cpm.util.SerieType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This process is used for updating fields
 * study.has_repeated_series, ct_serie.is_repeated
 * @author 212706300
 */
@Component
public class RepeatSeriesCheckProcess implements StudyPostProcess<CTSerie> {

    private static final Logger log = LoggerFactory.getLogger(RepeatSeriesCheckProcess.class);

    private Integer priority;

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
    * For CT series, we need to group series by type(dtype), then check the scan range * to define if
    * there are repeated series in the study
    * @param studyList
    * @param studyWithSeriesMap
    */
    @Override
    public void process(Collection<Study> studyList, Map<String, TreeSet<CTSerie>> studyWithSeriesMap) {
    log.info("start to process studies if study has repeated series, priority of process: {}, num of studies: {}",
            this.priority, studyList.size());

    for (Study study : studyList) {
        TreeSet<CTSerie> serieSet = studyWithSeriesMap.get(study.getLocalStudyId());
        // exclude series without slice location
        Set<CTSerie> filteredSeries = serieSet.stream()
              .filter(serie -> serie.getStartSliceLocation() != null)
              .filter(serie -> serie.getEndSliceLocation() != null)
              .collect(Collectors.toSet());
        if(filteredSeries.size() > 1) {
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
        }
    }

    log.info("end of process to check if study has repeated series");
    }

    @Override
    public Integer getPriority() {
        return null;
    }
}
