package com.maksim.problemService.service;


import com.maksim.problemService.dto.standings.TaskProgressResponseDto;
import com.maksim.problemService.dto.standings.UserProgressResponseDto;
import com.maksim.problemService.entity.ContestUser;
import com.maksim.problemService.entity.ContestUserTask;
import com.maksim.problemService.enums.Status;
import com.maksim.problemService.entity.keys.ContestUserId;
import com.maksim.problemService.entity.keys.ContestUserTaskId;
import com.maksim.problemService.event.StandingsUpdateEvent;
import com.maksim.problemService.exception.ResourceNotFoundException;
import com.maksim.problemService.repository.ContestRepository;
import com.maksim.problemService.repository.ContestUserRepository;
import com.maksim.problemService.repository.ContestUserTaskRepository;
import com.maksim.problemService.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandingsService {
    private final String REDIS_LEADERBOARD_PREFIX = "contest:leaderboard:";

    private final String REDIS_CONTEST_USER_DETAILS_PREFIX = "contest:details:";

    private final Duration CACHE_TTL = Duration.ofHours(1);

    private final StringRedisTemplate redisTemplate;

    private final ContestUserRepository cuRepository;

    private final ContestUserTaskRepository cutRepository;

    private final ProblemRepository problemRepository;

    private final ContestRepository contestRepository;

    private final ObjectMapper om;

    private String getLeaderboardKey(int contestId) {
        return REDIS_LEADERBOARD_PREFIX + contestId;
    }

    private String getUserDetailsKey(int contestId, int userId) {
        return REDIS_CONTEST_USER_DETAILS_PREFIX + contestId + ":user:" + userId;
    }

    public List<UserProgressResponseDto> getLeaderboard(int contestId, int page, int pageSize) {
        page--;
        int start = pageSize * page;
        int end = start + pageSize - 1;
        String leaderboardKey = getLeaderboardKey(contestId);

        if (!redisTemplate.hasKey(leaderboardKey)) {
            rebuildStandingsAndProgressCache(contestId);
        }

        var leaders = redisTemplate.opsForZSet().reverseRangeWithScores(getLeaderboardKey(contestId), start, end);

        var resultList = new ArrayList<UserProgressResponseDto>(leaders.size());
        int rank = 1;

        for (var tuple : leaders) {
            int userId = Integer.parseInt(tuple.getValue());
            var userProgress = getUserProgressHash(contestId, userId);
            if (userProgress == null) {
                userProgress = getUserProgressDb(contestId, userId);
                saveUserProgressHash(contestId, userProgress);
            }
            userProgress.setPlace(rank);
            resultList.add(userProgress);
            ++rank;
        }
        return resultList;
    }


    @Transactional
    public void handleUpdateEvent(StandingsUpdateEvent event) {
        int userId = event.getUserId();
        int contestId = event.getContestId();
        int taskId = event.getProblemId();

        var cu = getContestUser(contestId, userId);

        var cutKey = new ContestUserTaskId(contestId, userId, taskId);

        var cut = cutRepository.findById(cutKey)
                .orElseGet(() -> {
                    ContestUserTask cut2 = new ContestUserTask(cutKey);
                    cut2.setProblem(problemRepository.getReferenceById(taskId));
                    cut2.setContest(contestRepository.getReferenceById(contestId));
                    return cut2;
                });

        if (cut.isSolved()) return;

        cut.incAttempts();

        if (event.getStatus() == Status.OK) {
            cut.setSolved(true);
            int scoreForTask = 100;
            cut.setScore(scoreForTask);
            cut.setSolutionTime(event.getSubmissionTime());

            cu.addScore(scoreForTask);
        }

        cuRepository.save(cu);
        cutRepository.save(cut);

        updateStandingsAndProgressCache(cu, cut);
    }

    private void updateStandingsAndProgressCache(ContestUser contestUser, ContestUserTask contestUserTask) {
        int userId = contestUser.getId().getUserId();
        int contestId = contestUser.getId().getContestId();
        String leaderboardKey = getLeaderboardKey(contestId);
        String taskKey = getUserDetailsKey(contestId, userId);

        redisTemplate.opsForZSet().add(leaderboardKey, String.valueOf(userId), contestUser.getTotalScore());
        String taskJson;
        try {
            taskJson = om.writeValueAsString(convertToDto(contestUserTask));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        redisTemplate.opsForHash().put(taskKey, String.valueOf(contestUserTask.getId().getTaskId()), taskJson);
    }

    private void rebuildStandingsAndProgressCache(int contestId) {
        if (!contestRepository.existsById(contestId))
            throw new ResourceNotFoundException("There is no contest with id " + contestId);

        String key = getLeaderboardKey(contestId);
        redisTemplate.delete(key);
        var allContestants = cuRepository.findById_ContestId(contestId);
        if (allContestants.isEmpty()) return;

        Map<Integer, List<ContestUserTask>> userToTasks = cutRepository.findById_ContestId(contestId)
                .stream().collect(Collectors.groupingBy(t -> t.getId().getUserId()));

        for (var contestUser : allContestants) {
            int userId = contestUser.getId().getUserId();
            // zset updated
            redisTemplate.opsForZSet().add(key, String.valueOf(userId), contestUser.getTotalScore());

            var contestUserTasks = userToTasks.getOrDefault(userId, new ArrayList<>())
                    .stream().map(this::convertToDto).toList();

            var cu = new UserProgressResponseDto();
            cu.setUserId(userId);
            cu.setTaskProgress(contestUserTasks);
            // hash updated
            saveUserProgressHash(contestId, cu);
        }
    }

    private void saveUserProgressHash(int contestId, UserProgressResponseDto cu) {
        String key = getUserDetailsKey(contestId, cu.getUserId());
        var mapa = new HashMap<String, String>();

        for (var task : cu.getTaskProgress()) {
            try {
                String taskJson = om.writeValueAsString(task);
                mapa.put(String.valueOf(task.getTaskId()), taskJson);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        redisTemplate.opsForHash().putAll(key, mapa);
        redisTemplate.expire(key, CACHE_TTL);
    }


    private ContestUser getContestUser(int contestId, int userId) {
        System.out.println(contestId + " " + userId);
        return cuRepository.findById(new ContestUserId(userId, contestId)).
                orElseThrow(() -> new ResourceNotFoundException("User not registered in this contest"));
    }

    //  прогресс без места и totalScore
    private UserProgressResponseDto getUserProgressDb(int contestId, int userId) {
        var cu = getContestUser(contestId, userId);
        var cut = cutRepository.findById_ContestIdAndId_UserId(contestId, userId);
        var tasks = cut.stream().map(this::convertToDto).toList();
        return new UserProgressResponseDto(userId, 0, tasks, cu.getTotalScore());
    }

    // прогресс без места и totalScore
    private UserProgressResponseDto getUserProgressHash(int contestId, int userId) {
        var mapa = redisTemplate.opsForHash().entries(getUserDetailsKey(contestId, userId));
        if (mapa.isEmpty()) return null;

        var up = new UserProgressResponseDto();
        up.setUserId(userId);

        var tasks = new ArrayList<TaskProgressResponseDto>();
        try {
            for (var nextTask : mapa.entrySet()) {
                var taskProgress = om.readValue(nextTask.getValue().toString(), TaskProgressResponseDto.class);
                tasks.add(taskProgress);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        tasks.sort(Comparator.comparingInt(TaskProgressResponseDto::getTaskId));
        up.setTaskProgress(tasks);
        return up;
    }

    private TaskProgressResponseDto convertToDto(ContestUserTask cut) {
        return new TaskProgressResponseDto(cut.getId().getTaskId(),
                cut.isSolved(),
                cut.getAttempts(),
                cut.getSolutionTime() != null ? (int) Duration.between(cut.getContest().getStartTime(), cut.getSolutionTime()).getSeconds() : 0,
                cut.getScore()
        );
    }
}
