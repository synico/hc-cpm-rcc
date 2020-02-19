package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.Study;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MockStudyRepository extends StudyRepository {

    @Query(value = "select max(id) from study", nativeQuery = true)
    Long getMaxStudyId();

    @Query(value = "select max(id) from study where aet = (:aet) and to_char(study_date, 'yyyy-MM-dd') = (:studyDateChar)", nativeQuery = true)
    Long getMaxStudyIdByAetAndDate(@Param(value = "aet") String aet, @Param(value = "studyDateChar") String studyDateChar);

    Optional<Study> findByLocalStudyId(String localStudyId);

}
