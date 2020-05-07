package cn.gehc.cpm.process.xa;

import cn.gehc.cpm.domain.XASerie;
import cn.gehc.cpm.domain.XAStudy;
import cn.gehc.cpm.process.StudyPostProcess;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * This process is used for updating fields:
 * xa_study.protocol_key, xa_study.protocol_name
 *
 * @author 212706300
 */

@Component("xaStudyProtocolProcess")
public class XAStudyProtocolProcess implements StudyPostProcess<XAStudy, XASerie> {

    private static final Logger log = LoggerFactory.getLogger(XAStudyProtocolProcess.class);

    private Integer priority;

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public void process(Collection<XAStudy> studyList, Map<String, TreeSet<XASerie>> studyWithSeriesMap) {
        log.info("start to process xa studies and retrieve protocol , priority of process: {}, num of studies: {}",
                this.priority, studyList.size());

        for(XAStudy xaStudy : studyList) {
            TreeSet<XASerie> serieSet = studyWithSeriesMap.get(xaStudy.getLocalStudyId());
            XASerie firstXASerie = null;
            // select first serie of xa study
            Iterator<XASerie> ascItr = serieSet.iterator();
            while (ascItr.hasNext()) {
                XASerie tmpSerie = ascItr.next();
                if (tmpSerie != null
                        && StringUtils.isNotBlank(tmpSerie.getProtocolName())
                        && tmpSerie.getProtocolKey() > 0L) {
                    firstXASerie = tmpSerie;
                    break;
                }
            }

            if(firstXASerie == null) {
                log.info("Since there isn't valid serie(first serie with protocol name/key), {} will not be updated",
                        xaStudy.getLocalStudyId());
            } else {
                xaStudy.setProtocolKey(firstXASerie.getProtocolKey());
                xaStudy.setProtocolName(firstXASerie.getProtocolName());
            }
        }

        log.info("end of process studies for mr study protocol");
    }

    @Override
    public Integer getPriority() {
        return this.priority;
    }
}
