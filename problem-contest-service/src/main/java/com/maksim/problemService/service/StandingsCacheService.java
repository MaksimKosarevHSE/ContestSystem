package com.maksim.problemService.service;

import com.maksim.problemService.dto.standings.TaskProgressResponseDto;
import com.maksim.problemService.dto.standings.UserProgressResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StandingsCacheService {

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private static final String LEADERBOARD_PREFIX = "contest:leaderboard:";
    private static final String USER_DETAILS_PREFIX = "contest:details:";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    private String leaderboardKey(int contestId) {
        return LEADERBOARD_PREFIX + contestId;
    }

    private String userDetailsKey(int contestId, int userId) {
        return USER_DETAILS_PREFIX + contestId + ":user:" + userId;
    }

    public boolean existsLeaderboard(int contestId) {
        return redisTemplate.hasKey(leaderboardKey(contestId));
    }

    public void putLeaderboardScore(int contestId, int userId, int totalScore) {
        redisTemplate.opsForZSet().add(leaderboardKey(contestId), String.valueOf(userId), totalScore);
    }

    public Set<ZSetOperations.TypedTuple<String>> getLeaderboardRange(int contestId, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(leaderboardKey(contestId), start, end);
    }

    public void putUserTaskDetail(int contestId, int userId, int taskId, TaskProgressResponseDto taskDetail) {
        try {
            String taskJson = objectMapper.writeValueAsString(taskDetail);
            String key = userDetailsKey(contestId, userId);
            redisTemplate.opsForHash().put(key, String.valueOf(taskId), taskJson);
            redisTemplate.expire(key, CACHE_TTL);
        } catch (Exception e) {
            log.error("Serialization exception {}", e.getMessage());
        }
    }

    public Map<Integer, TaskProgressResponseDto> getUserTasksDetails(int contestId, int userId) {
        String key = userDetailsKey(contestId, userId);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) return Collections.emptyMap();

        Map<Integer, TaskProgressResponseDto> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            try {
                int taskId = Integer.parseInt(entry.getKey().toString());
                TaskProgressResponseDto dto = objectMapper.readValue(entry.getValue().toString(), TaskProgressResponseDto.class);
                result.put(taskId, dto);
            } catch (Exception e) {
                log.error("Deserialization exception {}", e.getMessage());
            }
        }
        return result;
    }

    public void deleteLeaderboard(int contestId) {
        redisTemplate.delete(leaderboardKey(contestId));
    }

    public void deleteUserDetails(int contestId, int userId) {
        redisTemplate.delete(userDetailsKey(contestId, userId));
    }

    public void rebuildFromDatabase(int contestId, List<UserProgressResponseDto> users) {
        String leaderboardKey = leaderboardKey(contestId);
        deleteLeaderboard(contestId);

        for (UserProgressResponseDto user : users) {

            redisTemplate.opsForZSet().add(leaderboardKey, String.valueOf(user.getUserId()), user.getScore());
            String userKey = userDetailsKey(contestId, user.getUserId());

            Map<String, String> taskMap = user.getTaskProgress().stream()
                    .collect(Collectors.toMap(
                            t -> String.valueOf(t.getTaskId()),
                            t -> {
                                try {
                                    return objectMapper.writeValueAsString(t);
                                } catch (Exception e) {
                                    log.error("Serialization exception {}", e.getMessage());
                                    throw new RuntimeException(e);
                                }
                            }
                    ));
            taskMap.values().removeIf(Objects::isNull);
            if (!taskMap.isEmpty()) {
                redisTemplate.opsForHash().putAll(userKey, taskMap);
                redisTemplate.expire(userKey, CACHE_TTL);
            }
        }
    }
}