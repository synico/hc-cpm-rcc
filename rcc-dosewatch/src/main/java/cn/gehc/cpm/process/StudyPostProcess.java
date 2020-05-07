package cn.gehc.cpm.process;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author 212706300
 * @since v1.1
 */

public interface StudyPostProcess<T1, T2> {

    /**
     * Manually set priority of process, the lower value means higher priority.
     * Process with higher priority will be invoked earlier.
     * @param priority
     */
    void setPriority(Integer priority);

    /**
     * Implementations need to implements this method to process incoming studies
     * @param studyList
     * @param studyWithSeriesMap
     */
    void process(Collection<T1> studyList, Map<String, TreeSet<T2>> studyWithSeriesMap);

    /**
     * return priority of process
     * @return priority
     */
    Integer getPriority();
}
