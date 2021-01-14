package cn.gehc.ii;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static cn.gehc.ii.util.JobDefinition.JOB_NAME;

@Slf4j
public class JobTest {

    @Test
    public void testJobDef() {
        log.info(JOB_NAME.name());
    }

}
