package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.Study;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StudyRepository extends PagingAndSortingRepository<Study, String> {

    List<Study> findByLocalStudyIdIn(Collection<String> localStudyIds);

    @Deprecated
    @Query(value = "select * from study where aet = ?1 and to_char(study_date, 'yyyy-MM-dd') = ?2 order by study_date ASC",
            nativeQuery = true)
    List<Study> findByAETAndStudyDateChar(String aet, String studyDateChar);

    @Query(value = "select * from study where org_id = ?1 and aet = ?2 and modality = ?3 and " +
            "to_char(study_date, 'yyyy-MM-dd') = ?4 order by study_date ASC", nativeQuery = true)
    List<Study> findByAEAndStudyDateChar(Long orgId, String aet, String deviceType, String studyDateChar);

}
