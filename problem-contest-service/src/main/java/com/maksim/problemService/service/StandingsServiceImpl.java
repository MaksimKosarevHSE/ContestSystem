package com.maksim.problemService.service;


import com.maksim.problemService.dto.PageResponseDto;
import com.maksim.problemService.dto.standings.TaskProgressResponseDto;
import com.maksim.problemService.dto.standings.UserProgressResponseDto;
import com.maksim.problemService.entity.Contest;
import com.maksim.problemService.entity.associative.ContestUser;
import com.maksim.problemService.entity.associative.ContestUserTask;
import com.maksim.problemService.enums.Status;
import com.maksim.problemService.entity.keys.ContestUserId;
import com.maksim.problemService.entity.keys.ContestUserTaskId;
import com.maksim.problemService.event.StandingsUpdateEvent;
import com.maksim.problemService.exception.AccessDeniedException;
import com.maksim.problemService.exception.ResourceNotFoundException;
import com.maksim.problemService.repository.ContestRepository;
import com.maksim.problemService.repository.associative.ContestUserRepository;
import com.maksim.problemService.repository.associative.ContestUserTaskRepository;
import com.maksim.problemService.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StandingsServiceImpl implements StandingsService {

    private final ContestUserRepository cuRepository;

    private final ContestUserTaskRepository cutRepository;

    private final ProblemRepository problemRepository;

    private final ContestRepository contestRepository;

    private final StandingsCacheService cacheService;

    @Transactional
    public void handleUpdateEvent(StandingsUpdateEvent event) {
        int userId = event.getUserId();
        int contestId = event.getContestId();
        int taskId = event.getProblemId();

        ContestUser cu = getContestUser(contestId, userId);
        ContestUserTaskId cutId = new ContestUserTaskId(contestId, userId, taskId);
        ContestUserTask cut = cutRepository.findById(cutId)
                .orElseGet(() -> createNewTask(cutId, contestId, taskId));

        if (cut.getSolved()) {
            return;
        }
        cut.incAttempts();

        if (event.getStatus() == Status.OK) {
            cut.setSolved(true);
            int scoreForTask = 100;
            cut.setScore(scoreForTask);
            cut.setSolutionTime(event.getSubmissionTime());
            cu.addScore(scoreForTask);
        }

        cutRepository.save(cut);
        cuRepository.save(cu);
        updateCache(cu, cut);
    }

    private void updateCache(ContestUser contestUser, ContestUserTask contestUserTask) {
        try {
            int userId = contestUser.getId().getUserId();
            int contestId = contestUser.getId().getContestId();
            int taskId = contestUserTask.getId().getTaskId();

            TaskProgressResponseDto taskDto = convertToDto(contestUserTask);

            cacheService.putUserTaskDetail(contestId, userId, taskId, taskDto);
            cacheService.putLeaderboardScore(contestId, userId, contestUser.getTotalScore());

        } catch (Exception ex) {
            log.error("Failed to update redis cache");
        }
    }


    private ContestUserTask createNewTask(ContestUserTaskId id, int contestId, int taskId) {
        ContestUserTask newTask = new ContestUserTask(id);
        newTask.setProblem(problemRepository.getReferenceById(taskId));
        newTask.setContest(contestRepository.getReferenceById(contestId));
        return newTask;
    }


    public PageResponseDto<UserProgressResponseDto> getLeaderboard(int contestId, int page, int pageSize) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        if (contest.getStartTime().isAfter(Instant.now())) {
            throw new AccessDeniedException("The contest has not started");
        }
        ensureCacheBuilt(contestId);

        page--;
        int start = pageSize * page;
        int end = start + pageSize - 1;

        var leaders = cacheService.getLeaderboardRange(contestId, start, end);
        Long totalElements = cacheService.getLeaderboardTotalSize(contestId);

        if (leaders == null || leaders.isEmpty()) {
            return PageResponseDto.emptyPage(UserProgressResponseDto.class);
        }

        List<UserProgressResponseDto> result = new ArrayList<>(leaders.size());
        int rank = start + 1;

        for (var tuple : leaders) {
            int userId = Integer.parseInt(tuple.getValue());
            int totalScore = tuple.getScore().intValue();

            Map<Integer, TaskProgressResponseDto> taskMap = cacheService.getUserTasksDetails(contestId, userId);

            List<TaskProgressResponseDto> tasks = new ArrayList<>(taskMap.values());

            UserProgressResponseDto dto = UserProgressResponseDto.of(userId, rank, tasks, totalScore);
            result.add(dto);
            rank++;
        }

        return new PageResponseDto<>(
                result,
                page + 1,
                pageSize,
                totalElements,
                Long.valueOf((totalElements + (pageSize - 1)) / pageSize).intValue()
        );
    }

    public UserProgressResponseDto getUserStandings(Integer contestId, Integer userId) {
        ContestUser contestUser = getContestUser(contestId, userId);
        if (contestUser.getContest().getStartTime().isAfter(Instant.now())) {
            throw new AccessDeniedException("The contest has not started");
        }
        ensureCacheBuilt(contestId);
        Map<Integer, TaskProgressResponseDto> tasks = cacheService.getUserTasksDetails(contestId, userId);
        Integer rank = cacheService.getUserRank(contestId, userId);
        Integer totalScore = cacheService.getUserScore(contestId, userId);
        return UserProgressResponseDto.of(userId, rank, new ArrayList<>(tasks.values()), totalScore);
    }

    private void ensureCacheBuilt(int contestId) {
        if (cacheService.existsLeaderboard(contestId)) {
            return;
        }
        rebuildCache(contestId);
    }

    private void rebuildCache(int contestId) {
        if (!contestRepository.existsById(contestId)) {
            throw new ResourceNotFoundException("Contest not found: " + contestId);
        }

        List<ContestUser> allContestants = cuRepository.findById_ContestId(contestId);
        if (allContestants.isEmpty()) {
            cacheService.rebuildFromDatabase(contestId, Collections.emptyList());
            return;
        }

        Map<Integer, List<ContestUserTask>> tasksByUser = cutRepository.findById_ContestId(contestId)
                .stream()
                .collect(Collectors.groupingBy(t -> t.getId().getUserId()));

        List<UserProgressResponseDto> users = new ArrayList<>(allContestants.size());
        for (ContestUser cu : allContestants) {
            int userId = cu.getId().getUserId();
            List<ContestUserTask> userTasks = tasksByUser.getOrDefault(userId, Collections.emptyList());
            List<TaskProgressResponseDto> taskDtos = userTasks.stream()
                    .map(this::convertToDto)
                    .toList();

            UserProgressResponseDto dto = UserProgressResponseDto.of(userId, 0, taskDtos, cu.getTotalScore());
            users.add(dto);
        }

        cacheService.rebuildFromDatabase(contestId, users);
    }


    private ContestUser getContestUser(int contestId, int userId) {
        System.out.println(contestId + " " + userId);
        return cuRepository.findById(new ContestUserId(userId, contestId)).
                orElseThrow(() -> new ResourceNotFoundException("User not registered in this contest"));
    }

    private TaskProgressResponseDto convertToDto(ContestUserTask cut) {
        return new TaskProgressResponseDto(cut.getId().getTaskId(),
                cut.getSolved(),
                cut.getAttempts(),
                cut.getSolutionTime() != null ? (int) Duration.between(cut.getContest().getStartTime(), cut.getSolutionTime()).getSeconds() : 0,
                cut.getScore()
        );
    }
}
