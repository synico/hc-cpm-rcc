package cn.gehc.cpm.job;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MRStudyJobTest {

    @Autowired
    private MRStudyJob mrStudyJob;

    @Test
    public void testGenerateMRStudies() {
        mrStudyJob.generateMRStudies();
    }
}
