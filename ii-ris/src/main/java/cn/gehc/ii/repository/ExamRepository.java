package cn.gehc.ii.repository;

import cn.gehc.ii.domain.Exam;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExamRepository extends PagingAndSortingRepository <Exam, String> {

}
