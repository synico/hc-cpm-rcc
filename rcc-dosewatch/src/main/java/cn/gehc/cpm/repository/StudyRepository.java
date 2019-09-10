package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.Study;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface StudyRepository extends PagingAndSortingRepository<Study, String> {

    List<Study> findByLocalStudyIdIn(Collection<String> localStudyIds);

}
