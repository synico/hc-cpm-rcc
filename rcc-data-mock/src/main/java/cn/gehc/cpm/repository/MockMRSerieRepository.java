package cn.gehc.cpm.repository;

import org.springframework.data.jpa.repository.Query;

public interface MockMRSerieRepository extends MRSerieRepository {

    @Query(value = "select max(id) from mr_serie", nativeQuery = true)
    Long getMaxSerieId();

}
