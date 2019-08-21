package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.HisExam;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface HisExamRepository extends PagingAndSortingRepository<HisExam, String> {

    List<HisExam> findBySheetIdIn(List<String> sheetids);
}
