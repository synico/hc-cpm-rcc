package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.NMSerie;
import cn.gehc.cpm.domain.SerieKey;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NMSerieRepository extends PagingAndSortingRepository<NMSerie, SerieKey> {
}
