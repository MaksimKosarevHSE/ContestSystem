package com.maksim.problemService.repository;

import com.maksim.problemService.dto.ProblemSignature;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.entity.ProblemConstraints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    @Query("select Problem.id, Problem.compileTimeLimit, Problem.timeLimit, Problem .memoryLimit from Problem where Problem.id=:id")
    ProblemConstraints getProblemConstraints(@Param("id") int id);

    @Query("select Problem.id, Problem.title, Problem.complexity from Problem")
    Page<ProblemSignature> getProblemsSignatures(Pageable pageable);
}
