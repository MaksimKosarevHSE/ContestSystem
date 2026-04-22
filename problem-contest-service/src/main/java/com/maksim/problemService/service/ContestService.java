package com.maksim.problemService.service;

import com.maksim.problemService.dto.PageResponseDto;
import com.maksim.problemService.dto.contest.ContestResponseDto;
import com.maksim.problemService.dto.contest.CreateContestDto;
import com.maksim.problemService.dto.contest.UpdateContestDto;
import com.maksim.problemService.dto.problem.ProblemResponseDto;

public interface ContestService {
    ContestResponseDto createContest(CreateContestDto dto, Integer userId);

    ContestResponseDto updateContest(Integer contestId, UpdateContestDto dto, Integer userId);

    void deleteContest(Integer contestId, Integer userId);

    ContestResponseDto getContestById(Integer contestId);

    PageResponseDto<ContestResponseDto> getPublicContests(Integer page, Integer pageSize);

    PageResponseDto<ContestResponseDto> getUsersContests(Integer userId, Integer page, Integer pageSize);

    ProblemResponseDto getProblem(Integer contestId, Integer problemId);

    void registerUser(Integer contestId, Integer userId);

    PageResponseDto<Integer> getRegisteredUsersIds(Integer contestId, Integer page, Integer pageSize);

}
