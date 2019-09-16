package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.Study;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface StudyRepository extends PagingAndSortingRepository<Study, String> {

    List<Study> findByLocalStudyIdIn(Collection<String> localStudyIds);

    @Query(value = "select * from study where aet = ?1 and to_char(study_date, 'yyyy-MM-dd') = ?2 order by study_date ASC", nativeQuery = true)
    List<Study> findByAETAndStudyDateChar(String aet, String studyDateChar);

    @Query(value = "select * from study where aet in (:aets) and to_char(study_date, 'yyyy-MM-dd') = (:studyDateChar) order by local_study_id ASC", nativeQuery = true)
    List<Study> findByAETsAndStudyDateChar(@Param(value = "aets") Set<String> aets, @Param(value = "studyDateChar") String studyDateChar);

}
