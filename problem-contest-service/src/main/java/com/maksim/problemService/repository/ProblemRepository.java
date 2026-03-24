package com.maksim.problemService.repository;

import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.dto.problem.ProblemConstraints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    @Query("select new com.maksim.problemService.entity.ProblemConstraints(p.id, p.compileTimeLimit, p.timeLimit, p.memoryLimit) from Problem p where p.id=:id and p.isPublic")
    Optional<ProblemConstraints> getProblemConstraints(@Param("id") int id);

    @Query("select new com.maksim.problemService.dto.problem.ProblemSignatureResponseDto(p.id, p.title, p.complexity) from Problem p where p.isPublic")
    Page<ProblemSignatureResponseDto> getProblemsSignatures(Pageable pageable);

}
