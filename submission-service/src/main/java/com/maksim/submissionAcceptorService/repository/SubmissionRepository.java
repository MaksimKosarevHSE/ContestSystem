package com.maksim.submissionAcceptorService.repository;

import com.maksim.submissionAcceptorService.dto.GetSubmissionDto;
import com.maksim.submissionAcceptorService.entity.Status;
import com.maksim.submissionAcceptorService.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("select new com.maksim.submissionAcceptorService.dto.GetSubmissionDto(p.id, p.userId, p.problemId, p.time, p.programmingLanguage, p.status, p.executionTime, p.usedMemory) from Submission p " +
            "where p.contestId = :contestId " +
            "order by p.id desc")
    List<GetSubmissionDto> customFindAll(Integer contestId);

    @Query("select new com.maksim.submissionAcceptorService.dto.GetSubmissionDto(p.id, p.userId, p.problemId, p.time, p.programmingLanguage, p.status, p.executionTime, p.usedMemory) " +
            "from Submission p" +
            " where p.problemId= :problemId and p.userId = :userId and p.contestId= :contestId" +
            " order by p.id desc")
    Page<GetSubmissionDto> getSubmissionByProblemIdAndUserId(Integer problemId, Integer userId, Integer contestId, Pageable pageable);

    @Query("select new com.maksim.submissionAcceptorService.dto.GetSubmissionDto(p.id, p.userId, p.problemId, p.time, p.programmingLanguage, p.status, p.executionTime, p.usedMemory) " +
            "from Submission p" +
            " where p.problemId= :problemId and p.status = :status and p.contestId= :contestId" +
            " order by p.id desc")
    Page<GetSubmissionDto> getSubmissionsByProblemIdAndStatus(Integer problemId, Integer contestId, Status status, Pageable pageable);

    @Query("select new com.maksim.submissionAcceptorService.dto.GetSubmissionDto(p.id, p.userId, p.problemId, p.time, p.programmingLanguage, p.status, p.executionTime, p.usedMemory) " +
            "from Submission p" +
            " where p.userId = :userId and p.contestId= :contestId" +
            " order by p.id desc")
    Page<GetSubmissionDto> getAllSubmissionsByUserId(Integer userId, Integer contestId, Pageable pageable);

    Page<GetSubmissionDto> getSubmissionByUserIdAndProblemIdAndContestId(Integer userId, Integer problemId, Integer contestId, Pageable pageable);

}
