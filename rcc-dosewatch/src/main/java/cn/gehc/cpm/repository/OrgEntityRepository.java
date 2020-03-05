package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.OrgEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface OrgEntityRepository extends PagingAndSortingRepository<OrgEntity, Long> {

    List<OrgEntity> findByOrgName(String name);

}
