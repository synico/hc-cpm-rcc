package cn.gehc.ii.repository;

import cn.gehc.ii.domain.NisExam;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExamRepository extends PagingAndSortingRepository <NisExam, String> {

}
