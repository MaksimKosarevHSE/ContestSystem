package com.maksim.problemService.repository.associative;

import com.maksim.problemService.entity.associative.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Long> {

    Optional<ContestProblem> findByContestIdAndProblemId(Integer contestId, Integer problemId);

    Boolean existsByProblemId(Integer problemId);
}
