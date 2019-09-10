package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.MRSerie;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MRSerieRepository extends PagingAndSortingRepository<MRSerie, String> {

    List<MRSerie> findByLocalStudyKeyIn(List<String> localStudyIds);

}
