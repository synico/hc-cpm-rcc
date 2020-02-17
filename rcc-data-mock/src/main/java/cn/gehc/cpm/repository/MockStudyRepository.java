package cn.gehc.cpm.repository;

import org.springframework.data.jpa.repository.Query;

public interface MockStudyRepository extends StudyRepository {

    @Query(value = "select max(id) from study", nativeQuery = true)
    Long getMaxStudyId();
}
