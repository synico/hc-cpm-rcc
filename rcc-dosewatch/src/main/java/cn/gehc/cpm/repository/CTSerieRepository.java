package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.CTSerie;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author 212706300
 */

public interface CTSerieRepository extends PagingAndSortingRepository<CTSerie, String> {

    List<CTSerie> findByLocalStudyKeyIn(List<String> localStudyIds);
}
