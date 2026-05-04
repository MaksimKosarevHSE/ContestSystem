package com.maksim.submissionAcceptorService.repository;

import com.maksim.common.enums.Status;
import com.maksim.submissionAcceptorService.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s WHERE s.id = :id AND s.contestId = :contestId")
    Optional<Submission> findContestSubmissionById(@Param("id") Long id, @Param("contestId") Integer contestId);

    @Query("SELECT s FROM Submission s WHERE s.id = :id AND s.contestId IS NULL")
    Optional<Submission> findProblemSetSubmissionById(@Param("id") Long id);

    @Query("SELECT s FROM Submission s " +
            "WHERE (:userId IS NULL OR s.userId = :userId) " +
            "AND (:problemId IS NULL OR s.problemId = :problemId) " +
            "AND (:contestId IS NULL OR s.contestId = :contestId) " +
            "AND (:status IS NULL OR s.status = :status)")
    Page<Submission> findAllFiltered(@Param("contestId") Integer contestId,
                                     @Param("problemId") Integer problemId,
                                     @Param("userId") Integer userId,
                                     @Param("status") Status status,
                                     Pageable pageable);
}
