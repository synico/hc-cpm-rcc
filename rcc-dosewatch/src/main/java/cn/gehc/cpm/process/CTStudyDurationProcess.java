package cn.gehc.cpm.process;

import cn.gehc.cpm.domain.CTSerie;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.repository.CTSerieRepository;
import cn.gehc.cpm.repository.StudyRepository;
import cn.gehc.cpm.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This process is used for update field
 * study.study_start_time, study.study_end_time, study.published
 * @author 212706300
 */
@Component
public class CTStudyDurationProcess implements StudyPostProcess {

    private static final Logger log = LoggerFactory.getLogger(CTStudyDurationProcess.class);

    private Integer priority;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private CTSerieRepository ctSerieRepository;

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @param studyList studies from job (dosewatch database)
     */
    @Override
    public void process(Collection<Study> studyList) {
        log.info("start to process studies for study duration, priority of process: {}, num of studies: {}",
                this.priority, studyList.size());
        // As some studies have been persisted to database in previous job, to avoid values of study been
        // overwritten, need to merge studies from job and database.
        List<String> studyIds = studyList.stream().map(s -> s.getLocalStudyId()).collect(Collectors.toList());
        List<Study> studyFromDB = studyRepository.findByLocalStudyIdIn(studyIds);
        studyList.stream().filter(s -> studyFromDB.contains(s)).forEach(studyFromDB::add);

        Map<String, TreeSet<CTSerie>> studyWithSeriesMap = new HashMap<>(studyFromDB.size());

        // retrieve all ct series belongs to studies from database
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
        for(Study study : studyList) {
            TreeSet<CTSerie> serieSet = studyWithSeriesMap.get(study.getLocalStudyId());
            if(serieSet != null && serieSet.size() > 0) {
                CTSerie firstCTSerie = null, lastCTSerie = null;
                // select first serie of study
                Iterator<CTSerie> ascItr = serieSet.iterator();
                while(ascItr.hasNext()) {
                    CTSerie tmpSerie = ascItr.next();
                    if(tmpSerie != null && tmpSerie.getSeriesDate() != null) {
                        firstCTSerie = tmpSerie;
                        break;
                    }
                }
                // select last serie of study
                Iterator<CTSerie> descItr = serieSet.descendingIterator();
                while(descItr.hasNext()) {
                    CTSerie tmpSerie = descItr.next();
                    if(tmpSerie != null
                        && tmpSerie.getSeriesDate() != null
                        && tmpSerie.getExposureTime() != null) {
                        lastCTSerie = tmpSerie;
                        break;
                    }
                }
                // check if first and last serie has been selected
                if(firstCTSerie == null || lastCTSerie == null) {
                    //can't calculate duration of this study, mark this study to be deleted
                    study.setPublished(Study.StudyStatus.MARK_FOR_DELETION.getStatusId());
                    continue;
                }
                //update start time of study
                study.setStudyStartTime(firstCTSerie.getSeriesDate());
                //update end time of study
                study.setStudyEndTime(DataUtil.getLastSerieDate(lastCTSerie));
            }
        }
    }

    @Override
    public Integer getPriority() {
        return priority;
    }
}
