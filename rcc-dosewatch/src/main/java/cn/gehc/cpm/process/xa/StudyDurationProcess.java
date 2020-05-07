package cn.gehc.cpm.process.xa;

import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.domain.XASerie;
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
 * study.study_start_time, study_end_time, study.published
 *
 * @author 212706300
 */

@Component("xaStudyDurationProcess")
public class StudyDurationProcess implements StudyPostProcess<Study, XASerie> {

    private static final Logger log = LoggerFactory.getLogger(StudyDurationProcess.class);

    private Integer priority;

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public void process(Collection<Study> studyList, Map<String, TreeSet<XASerie>> studyWithSeriesMap) {
        log.info("start to process studies for study duration, priority of process: {}, num of studies: {}",
                this.priority, studyList.size());

        for(Study study : studyList) {
            TreeSet<XASerie> serieSet = studyWithSeriesMap.get(study.getLocalStudyId());
            if(serieSet != null && !serieSet.isEmpty()) {
                XASerie firstXASerie = null, lastXASerie = null;
                // select first serie of study
                Iterator<XASerie> ascItr = serieSet.iterator();
                while(ascItr.hasNext()) {
                    XASerie tmpSerie = ascItr.next();
                    if(tmpSerie != null && tmpSerie.getSeriesDate() != null) {
                        firstXASerie = tmpSerie;
                        break;
                    }
                }
                // select last serie of study
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
                // check if first and last serie have been selected
                if(firstXASerie == null || lastXASerie == null) {
                    // duration of this study can't be calculated, mark this study to be deleted
                    study.setPublished(Study.StudyStatus.MARK_FOR_DELETION.getStatusId());
                    continue;
                }
                // update start time of study
                study.setStudyStartTime(firstXASerie.getSeriesDate());
                // update end time of study
                study.setStudyEndTime(DataUtil.getLastSerieDate(lastXASerie));
            }
        }

        log.info("end of process studies for study duration");
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }
}
