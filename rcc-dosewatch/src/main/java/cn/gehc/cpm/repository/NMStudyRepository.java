package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.NMStudy;
import cn.gehc.cpm.domain.StudyKey;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NMStudyRepository extends PagingAndSortingRepository<NMStudy, StudyKey> {
}
