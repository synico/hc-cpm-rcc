package cn.gehc.cpm.process.mr;

import cn.gehc.cpm.domain.MRSerie;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.process.StudyPostProcess;
import cn.gehc.cpm.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * This process is used for updating fields:
 * study.study_start_time, study.study_end_time, study.published
 *
 * @author 212706300
 */

@Component("mrStudyDurationProcess")
public class StudyDurationProcess implements StudyPostProcess<Study, MRSerie> {

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
    public void process(Collection<Study> studyList, Map<String, TreeSet<MRSerie>> studyWithSeriesMap) {
        log.info("start to process studies for study duration, priority of process: {}, num of studies: {}",
                this.priority, studyList.size());

        for (Study study : studyList) {
            TreeSet<MRSerie> serieSet = studyWithSeriesMap.get(study.getLocalStudyId());
            if (serieSet != null && !serieSet.isEmpty()) {
                MRSerie firstMRSerie = null, lastMRSerie = null;
                // select first serie of study
                Iterator<MRSerie> ascItr = serieSet.iterator();
                while (ascItr.hasNext()) {
                    MRSerie tmpSerie = ascItr.next();
                    if (tmpSerie != null && tmpSerie.getAcquisitionDatetime() != null) {
                        firstMRSerie = tmpSerie;
                        break;
                    }
                }
                // select last serie of study
                Iterator<MRSerie> descItr = serieSet.descendingIterator();
                while (descItr.hasNext()) {
                    MRSerie tmpSerie = descItr.next();
                    if (tmpSerie != null
                        && tmpSerie.getAcquisitionDatetime() != null
                        && tmpSerie.getAcquisitionDuration() != null) {
                        lastMRSerie = tmpSerie;
                        break;
                    }
                }
                // check if first and last serie have been selected
                if (firstMRSerie == null || lastMRSerie == null) {
                    // duration of this study can't be calculated, mark this study to be deleted
                    study.setPublished(Study.StudyStatus.MARK_FOR_DELETION.getStatusId());
                    continue;
                }
                // update start time of study
                study.setStudyStartTime(firstMRSerie.getAcquisitionDatetime());
                // update end time of study
                study.setStudyEndTime(DataUtil.getLastSerieDate(lastMRSerie));
            }
        }

        log.info("end of process studies for study duration");
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }
}
