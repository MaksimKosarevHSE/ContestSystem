package com.maksim.problemService.service;

import com.maksim.problemService.dto.standings.TaskProgressResponseDto;
import com.maksim.problemService.dto.standings.UserProgressResponseDto;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StandingsCacheService {
    boolean existsLeaderboard(int contestId);

    void putLeaderboardScore(int contestId, int userId, int totalScore);

    Integer getUserRank(int contestId, int userId);

    Integer getUserScore(int contestId, int userId);

    Set<ZSetOperations.TypedTuple<String>> getLeaderboardRange(int contestId, long start, long end);

    Long getLeaderboardTotalSize(int contestId);

    void putUserTaskDetail(int contestId, int userId, int taskId, TaskProgressResponseDto taskDetail);

    Map<Integer, TaskProgressResponseDto> getUserTasksDetails(int contestId, int userId);

    void rebuildFromDatabase(int contestId, List<UserProgressResponseDto> users);

}
