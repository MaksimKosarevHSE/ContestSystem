package com.maksim.problemService.service;

import com.maksim.problemService.dto.PageResponseDto;
import com.maksim.problemService.dto.problem.ProblemConstrainsResponseDto;
import com.maksim.problemService.dto.problem.ProblemCreateDto;
import com.maksim.problemService.dto.problem.ProblemResponseDto;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.dto.problem.ProblemUpdateDto;

public interface ProblemService {
    ProblemResponseDto createProblem(ProblemCreateDto problemCreateDto, Integer creatorId);

    ProblemResponseDto updateProblem(Integer id, ProblemUpdateDto dto, Integer userId);

    void deleteProblem(Integer id, Integer userId);

    PageResponseDto<ProblemSignatureResponseDto> getPublicProblemsSignatures(Integer pageNumber, Integer pageSize);

    PageResponseDto<ProblemSignatureResponseDto> getUsersProblemsSignatures(Integer userId, Integer pageNumber, Integer pageSize);

    ProblemResponseDto getPublicProblemById(Integer id);

    ProblemResponseDto getUsersProblem(Integer userId, Integer problemId);

    ProblemConstrainsResponseDto getProblemConstraints(Integer contestId, Integer problemId);

}
