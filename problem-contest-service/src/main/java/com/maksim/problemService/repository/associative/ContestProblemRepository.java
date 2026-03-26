package com.maksim.problemService.repository.associative;

import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.entity.associative.ContestProblem;
import com.maksim.problemService.entity.keys.ContestProblemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, ContestProblemId> {

    Optional<ContestProblem> findByContestIdAndProblemId(Integer contestId, Integer problemId);

    @Query("select cp.problem from ContestProblem cp where cp.problem.id = :problemId and cp.contest.id = :contestId")
    Optional<Problem> getProblem(Integer contestId, Integer problemId);

    Optional<ContestProblem> findByProblemId(Integer problemId);

    List<ContestProblem> findByContestId(Integer contestId);

    void deleteByContestId(int contestId);

    boolean existsByProblemId(int problemId);
}
