package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.OrgInfo;
import cn.gehc.cpm.repository.OrgInfoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrgInfoTest {

    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Test
    public void testOrgCreation() {
        List<OrgInfo> orgInfoList = new ArrayList<>();
        OrgInfo orgInfo;
        for(int i = 0; i < 10; i++) {
            orgInfo = new OrgInfo();
            orgInfo.setOrgName("Hospital-" + i);
            orgInfo.setOrgLevel(2);
            orgInfoList.add(orgInfo);
        }
        orgInfoRepository.saveAll(orgInfoList);
    }
}
