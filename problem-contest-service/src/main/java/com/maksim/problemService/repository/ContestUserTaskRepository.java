package com.maksim.problemService.repository;

import com.maksim.problemService.entity.associative.ContestUserTask;
import com.maksim.problemService.entity.keys.ContestUserTaskId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestUserTaskRepository extends JpaRepository<ContestUserTask, ContestUserTaskId> {
    List<ContestUserTask> findById_ContestIdAndId_UserId(int idContestId, int idUserId);
    List<ContestUserTask> findById_ContestIdAndId_UserIdAndId_TaskId(int idContestId, int idUserId, int taskId);
    List<ContestUserTask> findById_ContestId(int idContestId);
}
