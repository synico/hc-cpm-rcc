package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.XASerie;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author 212706300
 */

public interface XASerieRepository extends PagingAndSortingRepository<XASerie, String> {

    List<XASerie> findByLocalStudyKeyIn(List<String> localStudyIds);

}
