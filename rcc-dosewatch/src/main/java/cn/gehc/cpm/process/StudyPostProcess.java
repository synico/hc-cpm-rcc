package cn.gehc.cpm.process;

import cn.gehc.cpm.domain.Study;

import java.util.Collection;

/**
 * @author 212706300
 */
public interface StudyPostProcess {

    /**
     * Manually set priority of process, the lower value means higher priority.
     * Process with higher priority will be invoked earlier.
     * @param priority
     */
    void setPriority(Integer priority);

    /**
     * Implementations need to implements this method to process incoming studies
     * @param studyList
     */
    void process(Collection<Study> studyList);

    /**
     * return priority of process
     * @return
     */
    Integer getPriority();
}
