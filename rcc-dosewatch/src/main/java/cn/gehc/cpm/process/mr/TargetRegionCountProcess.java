package cn.gehc.cpm.process.mr;

import cn.gehc.cpm.domain.MRSerie;
import cn.gehc.cpm.domain.Study;
import cn.gehc.cpm.process.StudyPostProcess;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

/**
 * This process is used for updating fields:
 * study.target_region_count
 *
 * @author 212706300
 */

@Component("mrTargetRegionCountProcess")
public class TargetRegionCountProcess implements StudyPostProcess<MRSerie> {

    private static final Logger log = LoggerFactory.getLogger(TargetRegionCountProcess.class);

    private Integer priority;

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @param studyList
     * @param studyWithSeriesMap
     */
    @Override
    public void process(Collection<Study> studyList, Map<String, TreeSet<MRSerie>> studyWithSeriesMap) {
        log.info("start to process studies for target region count, priority of process: {}, num of studies: {}",
                this.priority, studyList.size());

        for(Study study : studyList) {
            TreeSet<MRSerie> serieSet = studyWithSeriesMap.get(study.getLocalStudyId());
            // to calculate target region count by serie
            Long targetRegionCount = serieSet.stream().map(mrse -> mrse.getProtocolName())
                    .filter(protocolName -> StringUtils.isNotBlank(protocolName))
                    .distinct()
                    .count();
            if(targetRegionCount > 1 && log.isDebugEnabled()) {
                log.debug("***************************************************************");
                log.debug("study: {}, target_region: {}, target region count: {}",
                        study.getLocalStudyId(),
                        serieSet.stream().map(mrse -> mrse.getProtocolName()).distinct().reduce((x, y) -> x + "," + y).get(),
                        targetRegionCount);
                log.debug("***************************************************************");
            }
            study.setTargetRegionCount(targetRegionCount.intValue());
            log.debug("study: {}, target region count: {}", study.getLocalStudyId(), study.getTargetRegionCount());
        }

        log.info("end of process studies for calculating target region count");
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }
}
