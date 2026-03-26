package com.maksim.problemService.repository.associative;

import com.maksim.problemService.entity.Contest;
import com.maksim.problemService.entity.associative.ContestUser;
import com.maksim.problemService.entity.keys.ContestUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface ContestUserRepository extends JpaRepository<ContestUser, ContestUserId> {

    Optional<ContestUser> findById_ContestIdAndId_UserId(Integer idContestId, Integer idUserId);

    List<ContestUser> findById_ContestId(Integer contestId);

    Page<ContestUser> findById_ContestId(Integer contestId, Pageable pageable);

    void deleteById_ContestId(Integer contestId);

    @Query("SELECT cu.contest FROM ContestUser cu WHERE cu.id.userId = :userId ORDER BY cu.contest.startTime DESC")
    Page<Contest> findContestsByUserId(@Param("userId") int userId, Pageable pageable);
}
