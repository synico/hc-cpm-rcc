/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.gehc.cpm.repository;

import cn.gehc.cpm.domain.RisExam;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 *
 * @author 212579464
 */
public interface RisExamRepository extends PagingAndSortingRepository<RisExam, String> {

    List<RisExam> findByRequisitionIdIn(List<String> requisitionIds);

}
