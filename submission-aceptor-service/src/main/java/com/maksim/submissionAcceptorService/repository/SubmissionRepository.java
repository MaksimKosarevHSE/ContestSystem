package com.maksim.submissionAcceptorService.repository;

import com.maksim.submissionAcceptorService.dto.GetSubmissionDto;
import com.maksim.submissionAcceptorService.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("select new com.maksim.submissionAcceptorService.dto.GetSubmissionDto(p.id, p.userId, p.problemId, p.time, p.programmingLanguage, p.status, p.executionTime, p.usedMemory) from Submission p order by p.id desc")
    List<GetSubmissionDto> customFindAll();

    @Query("select new com.maksim.submissionAcceptorService.dto.GetSubmissionDto(p.id, p.userId, p.problemId, p.time, p.programmingLanguage, p.status, p.executionTime, p.usedMemory) " +
            "from Submission p" +
            " where p.problemId= :problemId and p.userId = :userId" +
            " order by p.id desc")
    Page<GetSubmissionDto> getSubmissionByProblemIdAndUserId(Integer problemId, Integer userId, Pageable pageable);

    Page<GetSubmissionDto> getSubmissionByProblemIdAndStatus(Integer problemId, PageRequest of);
}
