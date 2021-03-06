package cn.gehc.cpm.job;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CTStudyJobTest {

    @Autowired
    private CTStudyJob ctStudyJob;

    @Test
    public void testGenerateCTStudies() {
        ctStudyJob.generateCTStudies();
    }
}
