package cn.gehc.cpm.job;

import cn.gehc.cpm.domain.OrgEntity;
import cn.gehc.cpm.repository.OrgEntityRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrgEntityTest {

    @Autowired
    private OrgEntityRepository orgEntityRepository;

    @Test
    public void testOrgCreation() {
        List<OrgEntity> orgEntityList = new ArrayList<>();
        OrgEntity orgEntity;
        for(int i = 0; i < 10; i++) {
            orgEntity = new OrgEntity();
            orgEntity.setOrgName("Hospital-" + i);
            orgEntity.setOrgLevel(2);
            orgEntityList.add(orgEntity);
        }
        Iterable<OrgEntity> savedOrgs = orgEntityRepository.saveAll(orgEntityList);
        savedOrgs.forEach(org -> {
//            System.out.println(org.getOrgId());
        });
    }

    @Test
    public void testFindOrg() {
        List<OrgEntity> orgEntityList = orgEntityRepository.findByOrgName("Hospital-2");
        orgEntityList.stream().forEach(orgEntity -> {
            System.out.println("org_id: " + orgEntity.getOrgId());
        });
    }
}
