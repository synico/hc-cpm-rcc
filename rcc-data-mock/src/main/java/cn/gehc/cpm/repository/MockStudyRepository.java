package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.Study;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MockStudyRepository extends StudyRepository {

    @Query(value = "select max(id) from study", nativeQuery = true)
    Long getMaxStudyId();

    @Query(value = "select max(id) from study where device_key = (:dk) and to_char(study_date, 'yyyy-MM-dd') = (:studyDateChar)", nativeQuery = true)
    Long getMaxStudyIdByDkAndDate(@Param(value = "dk") String dk, @Param(value = "studyDateChar") String studyDateChar);

    Optional<Study> findByLocalStudyId(String localStudyId);

}
