package com.maksim.problemService.repository;

import com.maksim.problemService.entity.associative.ContestUser;
import com.maksim.problemService.entity.keys.ContestUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContestUserRepository extends JpaRepository<ContestUser, ContestUserId> {
    List<ContestUser> findById_ContestId(int idContestId);

    Optional<ContestUser> findById_ContestIdAndId_UserId(Integer idContestId, Integer idUserId);
}
