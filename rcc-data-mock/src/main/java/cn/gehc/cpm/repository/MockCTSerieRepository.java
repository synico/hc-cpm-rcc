package cn.gehc.cpm.repository;

import org.springframework.data.jpa.repository.Query;

public interface MockCTSerieRepository extends CTSerieRepository {

    @Query(value = "select max(id) from ct_serie", nativeQuery = true)
    Long getMaxSerieId();

}
