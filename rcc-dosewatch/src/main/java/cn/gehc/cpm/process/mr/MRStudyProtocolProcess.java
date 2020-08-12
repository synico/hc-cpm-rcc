package cn.gehc.cpm.process.mr;

import cn.gehc.cpm.domain.MRSerie;
import cn.gehc.cpm.domain.MRStudy;
import cn.gehc.cpm.process.StudyPostProcess;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This process is used for updating fields:
 * mr_study.protocol_key, mr_study.protocol_name
 *
 * @author 212706300
 */

@Component("mrStudyProtocolProcess")
public class MRStudyProtocolProcess implements StudyPostProcess<MRStudy, MRSerie> {

    private static final Logger log = LoggerFactory.getLogger(MRStudyProtocolProcess.class);

    private Integer priority;

    /**
     * @param priority
     */
    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * Retrieve first serie of each mr study, then choose protocol key and protocol name and
     * assign values to mr study
     * @param studyList
     * @param studyWithSeriesMap
     */
    @Override
    public void process(Collection<MRStudy> studyList, Map<String, Set<MRSerie>> studyWithSeriesMap) {
        log.info("start to process mr studies and retrieve protocol , priority of process: {}, num of studies: {}",
                this.priority, studyList.size());

        for (MRStudy mrStudy : studyList) {
            Set<MRSerie> serieSet = studyWithSeriesMap.get(mrStudy.getLocalStudyId());
            MRSerie firstMRSerie = null;
            // select first serie of mr study
            Iterator<MRSerie> ascItr = serieSet.iterator();
            while (ascItr.hasNext()) {
                MRSerie tmpSerie = ascItr.next();
                if (tmpSerie != null
                        && StringUtils.isNotBlank(tmpSerie.getProtocolName())
                        && tmpSerie.getProtocolKey() > 0L) {
                    firstMRSerie = tmpSerie;
                    break;
                }
            }

            if (firstMRSerie == null) {
                log.info("Since there isn't valid serie(first serie with protocol name/key), {} will not be updated",
                        mrStudy.getLocalStudyId());
            } else {
                mrStudy.setProtocolKey(firstMRSerie.getProtocolKey());
                mrStudy.setProtocolName(firstMRSerie.getProtocolName());
            }
        }

        log.info("end of process studies for mr study protocol");
    }

    /**
     * @return priority
     */
    @Override
    public Integer getPriority() {
        return this.priority;
    }

}
