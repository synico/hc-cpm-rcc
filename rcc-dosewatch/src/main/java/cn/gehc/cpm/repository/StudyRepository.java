package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.Study;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface StudyRepository extends PagingAndSortingRepository<Study, String> {

    List<Study> findByLocalStudyIdIn(Collection<String> localStudyIds);

    @Query(value = "select * from study where aet = ?1 and to_char(study_date, 'yyyy-MM-dd') = ?2 order by study_date ASC", nativeQuery = true)
    List<Study> findByAETAndStudyDateChar(String aet, String studyDateChar);

}
