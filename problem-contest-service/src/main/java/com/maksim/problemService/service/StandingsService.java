package com.maksim.problemService.service;


import com.maksim.problemService.dto.standings.TaskProgressDto;
import com.maksim.problemService.dto.standings.UserProgressDto;
import com.maksim.problemService.entity.ContestUser;
import com.maksim.problemService.entity.ContestUserTask;
import com.maksim.problemService.entity.Status;
import com.maksim.problemService.entity.keys.ContestUserId;
import com.maksim.problemService.entity.keys.ContestUserTaskId;
import com.maksim.problemService.event.StandingsUpdateEvent;
import com.maksim.problemService.repository.ContestUserRepository;
import com.maksim.problemService.repository.ContestUserTaskRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StandingsService {
    private final String REDIS_LEADERBOARD_PREFIX = "contest:leaderboard:";

    private final String REDIS_CONTEST_USER_DETAILS_PREFIX = "contest:details:";

    private final Duration CACHE_TTL = Duration.ofHours(1);

    private final StringRedisTemplate redisTemplate;

    private final ContestUserRepository cuRepository;

    private final ContestUserTaskRepository cutRepository;

    private final ObjectMapper om;
    private final ContestUserTaskRepository contestUserTaskRepository;

    public StandingsService(StringRedisTemplate redisTemplate, ContestUserRepository cuRep, ContestUserTaskRepository cutRep, ObjectMapper om, ContestUserTaskRepository contestUserTaskRepository) {
        this.redisTemplate = redisTemplate;
        this.cutRepository = cutRep;
        this.cuRepository = cuRep;
        this.om = om;
        this.contestUserTaskRepository = contestUserTaskRepository;
    }

    private String getLeaderboardKey(int contestId) {
        return REDIS_LEADERBOARD_PREFIX + contestId;
    }

    private String getUserDetailsKey(int contestId, int userId) {
        return REDIS_CONTEST_USER_DETAILS_PREFIX + contestId + ":user:" + userId;
    }

    public List<UserProgressDto> getLeaderboard(int contestId, int page, int pageSize) {
        int start = pageSize * page;
        int end = start + page - 1;
        String leaderboardKey = getLeaderboardKey(contestId);

        if (!redisTemplate.hasKey(leaderboardKey)) {
            rebuildStandingsAndProgressCache(contestId);
        }

        var leaders = redisTemplate.opsForZSet().reverseRangeWithScores(getLeaderboardKey(contestId), start, end);

        var resultList = new ArrayList<UserProgressDto>(leaders.size());
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


    public void handleUpdateEvent(StandingsUpdateEvent event) {
        int userId = event.getUserId();
        int contestId = event.getContestId();
        int taskId = event.getProblemId();

        var cu = cuRepository.findById(new ContestUserId(userId, contestId))
                .orElseThrow(() -> new RuntimeException("User is not registered"));

        var cutKey = new ContestUserTaskId(contestId, userId, taskId);

        var cut = cutRepository.findById(cutKey)
                .orElseGet(() -> new ContestUserTask(cutKey));

        if (cut.getIsSolved()) return;

        cut.incAttempts();

        if (event.getStatus() == Status.OK) {
            cut.setIsSolved(true);
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
            taskJson = om.writeValueAsString(contestUserTask);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        redisTemplate.opsForHash().put(taskKey, String.valueOf(contestUserTask.getId().getTaskId()), taskJson);
//        int totalFine = contestUserTask
//                .stream().mapToInt(t -> t.getFine()).sum();

//        redisTemplate.opsForHash().put(taskKey, "fine", )
    }

    private void rebuildStandingsAndProgressCache(int contestId) {
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
                    .stream().map(this::convert).toList();
//            int totalFine = contestUserTasks
//                    .stream().mapToInt(t -> t.getFine()).sum();

            var cu = new UserProgressDto();
            cu.setUserId(userId);
            cu.setTaskProgress(contestUserTasks);
//            cu.setFine(totalFine);

            // hash updated
            saveUserProgressHash(contestId, cu);
        }
    }

    private void saveUserProgressHash(int contestId, UserProgressDto cu) {
        String key = getUserDetailsKey(contestId, cu.getUserId());
        var mapa = new HashMap<String, String>();
//        mapa.put("fine", String.valueOf(cu.getFine()));
        for (var task : cu.getTaskProgress()) {
            try {
                String taskJson = om.writeValueAsString(task);
                mapa.put(String.valueOf(task.getTaskId()), taskJson);
            } catch (Exception ex) {
                throw new RuntimeException();
            }
        }
        redisTemplate.opsForHash().putAll(key, mapa);
        redisTemplate.expire(key, CACHE_TTL);
    }


    //  прогресс без места и totalScore
    private UserProgressDto getUserProgressDb(int contestId, int userId) {
        var cuOpt = cuRepository.findById(new ContestUserId(userId, contestId));
        if (cuOpt.isEmpty()) {
            return null;
        }
        var cu = cuOpt.get();
        var cut = contestUserTaskRepository.findById_ContestIdAndId_UserId(contestId, userId);
//        int totalFine = cut.stream().mapToInt(t -> t.getFine()).sum();
        var tasks = cut.stream().map(this::convert).toList();
        return new UserProgressDto(userId, 0, tasks, cu.getTotalScore());
    }

    // прогресс без места и totalScore
    private UserProgressDto getUserProgressHash(int contestId, int userId) {
        var mapa = redisTemplate.opsForHash().entries(getUserDetailsKey(userId, contestId));
        if (mapa == null) return null;

//        var fineObj = mapa.remove("fine");
//        int fine = fineObj != null ? Integer.parseInt(fineObj.toString()) : 0;

        var up = new UserProgressDto();
        up.setUserId(userId);
//        up.setFine(fine);

        var tasks = new ArrayList<TaskProgressDto>();
        try {
            for (var nextTask : mapa.entrySet()) {
                var taskProgress = om.readValue(nextTask.getValue().toString(), TaskProgressDto.class);
                tasks.add(taskProgress);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        Collections.sort(tasks, (t1, t2) -> t1.getTaskId() - t2.getTaskId());
        up.setTaskProgress(tasks);
        return up;
    }

    private TaskProgressDto convert(ContestUserTask cut) {
        return new TaskProgressDto(cut.getId().getTaskId(),
                cut.getIsSolved(),
                cut.getAttempts(),
                (int) Duration.between(cut.getContest().getStartTime(), cut.getSolutionTime()).getSeconds(),
                cut.getScore()
        );
    }
}
