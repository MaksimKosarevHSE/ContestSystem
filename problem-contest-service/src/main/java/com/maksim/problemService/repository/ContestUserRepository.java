package com.maksim.problemService.repository;

import com.maksim.problemService.dto.UserScoreView;
import com.maksim.problemService.entity.ContestUser;
import com.maksim.problemService.entity.ContestUserTask;
import com.maksim.problemService.entity.keys.ContestUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ContestUserRepository extends JpaRepository<ContestUser, ContestUserId> {

    @Query("select cu.id.userId as userId, cu.totalScore as totalScore from ContestUser cu where cu.contest.id = :contestId order by cu.totalScore desc")
    List<UserScoreView> getLeaderboardData(@Param("contestId") Integer contestId);
    // userId, totalScore,

    List<ContestUser> findById_ContestId(int idContestId);

}
