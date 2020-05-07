package cn.gehc.cpm.process.ct;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.process.StudyPostProcess;
import cn.gehc.cpm.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * This process is used for updating fields:
 * study.study_start_time, study.study_end_time, study.published
 *
 * @author 212706300
 */

@Component("ctStudyDurationProcess")
public class StudyDurationProcess implements StudyPostProcess<Study, CTSerie> {

    private static final Logger log = LoggerFactory.getLogger(StudyDurationProcess.class);

    private Integer priority;

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @param studyList studies from job (dosewatch database)
     * @param studyWithSeriesMap
     */
    @Override
    public void process(Collection<Study> studyList, Map<String, TreeSet<CTSerie>> studyWithSeriesMap) {
        log.info("start to process studies for study duration, priority of process: {}, num of studies: {}",
                this.priority, studyList.size());

        for (Study study : studyList) {
            TreeSet<CTSerie> serieSet = studyWithSeriesMap.get(study.getLocalStudyId());
            if (serieSet != null && !serieSet.isEmpty()) {
                CTSerie firstCTSerie = null, lastCTSerie = null;
                // select first serie of study
                Iterator<CTSerie> ascItr = serieSet.iterator();
                while (ascItr.hasNext()) {
                    CTSerie tmpSerie = ascItr.next();
                    if (tmpSerie != null && tmpSerie.getSeriesDate() != null) {
                        firstCTSerie = tmpSerie;
                        break;
                    }
                }
                // select last serie of study
                Iterator<CTSerie> descItr = serieSet.descendingIterator();
                while (descItr.hasNext()) {
                    CTSerie tmpSerie = descItr.next();
                    if (tmpSerie != null
                        && tmpSerie.getSeriesDate() != null
                        && tmpSerie.getExposureTime() != null) {
                        lastCTSerie = tmpSerie;
                        break;
                    }
                }
                // check if first and last serie have been selected
                if (firstCTSerie == null || lastCTSerie == null) {
                    // can't calculate duration of this study, mark this study to be deleted
                    study.setPublished(Study.StudyStatus.MARK_FOR_DELETION.getStatusId());
                    continue;
                }
                // update start time of study
                study.setStudyStartTime(firstCTSerie.getSeriesDate());
                // update end time of study
                study.setStudyEndTime(DataUtil.getLastSerieDate(lastCTSerie));
            }
        }

        log.info("end of process studies for study duration");
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }
}
