package cn.gehc.ii;

import cn.gehc.ii.util.JobDefinition;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import static cn.gehc.ii.util.JobDefinition.*;

@Slf4j
public class JobTest {

    @Test
    public void testJobDef() {
        log.info(JOB_NAME.name());
    }

}
