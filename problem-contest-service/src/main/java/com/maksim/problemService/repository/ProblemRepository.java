package com.maksim.problemService.repository;

import com.maksim.problemService.entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ProblemRepository extends JpaRepository<Problem, Integer> {

    Optional<Problem> findByIdAndIsPublicTrue(Integer id);

    Page<Problem> findByIsPublicTrue(Pageable pageable);

    Page<Problem> findByCreatorId(Integer userId, Pageable pageable);

    Optional<Problem> findByCreatorIdAndId(Integer creatorId, Integer id);

    @Query("SELECT p FROM Problem p WHERE p.creatorId = :authorId AND p.id IN :problemIds")
    List<Problem> findProblemsByAuthorAndIds(@Param("authorId") int authorId, @Param("problemIds") List<Integer> problemIds);
}
