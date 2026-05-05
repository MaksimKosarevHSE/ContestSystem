package com.maksim.problemService.service;


import com.maksim.problemService.client.JudgingServiceClient;
import com.maksim.common.dto.PageResponseDto;
import com.maksim.problemService.dto.mapper.ProblemMapper;
import com.maksim.common.dto.problem.ProblemConstrainsResponseDto;
import com.maksim.common.dto.problem.SaveTestCasesRequestDto;
import com.maksim.problemService.dto.problem.ProblemCreateDto;
import com.maksim.problemService.dto.problem.ProblemResponseDto;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.dto.problem.ProblemUpdateDto;
import com.maksim.problemService.entity.associative.ContestProblem;
import com.maksim.problemService.exception.ResourceNotFoundException;
import com.maksim.problemService.exception.ForbiddenException;
import com.maksim.problemService.exception.BadRequestException;
import com.maksim.problemService.repository.associative.ContestProblemRepository;
import com.maksim.problemService.validators.ProblemValidator;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.repository.ProblemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl implements ProblemService {
    private final ProblemRepository problemRepository;

    private final ProblemValidator problemValidator;

    private final ContestProblemRepository contestProblemRepository;

    private final JudgingServiceClient judgingServiceClient;

    private final ProblemMapper problemMapper;


    @Transactional
    public ProblemResponseDto createProblem(ProblemCreateDto problemCreateDto, Integer creatorId) {
        problemValidator.validate(problemCreateDto);
        Problem problem = problemMapper.toEntity(problemCreateDto);
        problem.setCreatorId(creatorId);
        problem = problemRepository.save(problem);

        SaveTestCasesRequestDto saveTestsDto = problemCreateDto.toSaveTestCasesRequestDto()
                .withProblemId(problem.getId());
        judgingServiceClient.saveTestCases(saveTestsDto);

        return problemMapper.toResponseDto(problem);
    }

    @Transactional
    public ProblemResponseDto updateProblem(Integer id, ProblemUpdateDto dto, Integer userId) {
        Problem problem = getOwnedProblem(id, userId);
        problemMapper.updateFromPatch(problem, dto);
        problem = problemRepository.save(problem);
        return problemMapper.toResponseDto(problem);
    }

    @Transactional
    public void deleteProblem(Integer id, Integer userId) {
        Problem problem = getOwnedProblem(id, userId);
        if (contestProblemRepository.existsByProblemId(id)) {
            throw new BadRequestException("Problem is used in contests, can't delete");
        }
        problemRepository.delete(problem);
    }

    public PageResponseDto<ProblemSignatureResponseDto> getPublicProblemsSignatures(Integer pageNumber, Integer pageSize) {
        Page<ProblemSignatureResponseDto> page = problemRepository
                .findByIsPublicTrue(PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "id")))
                .map(problemMapper::toProblemSignature);
        return PageResponseDto.from(page);
    }

    public PageResponseDto<ProblemSignatureResponseDto> getUsersProblemsSignatures(Integer userId, Integer pageNumber, Integer pageSize) {
        Page<ProblemSignatureResponseDto> page = problemRepository.findByCreatorId(userId, PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "id")))
                .map(problemMapper::toProblemSignature);
        return PageResponseDto.from(page);
    }

    public ProblemResponseDto getPublicProblemById(Integer id) {
        Problem problem = problemRepository.findByIdAndIsPublicTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        return problemMapper.toResponseDto(problem);
    }

    public ProblemResponseDto getUsersProblem(Integer userId, Integer problemId) {
        Problem problem = problemRepository.findByCreatorIdAndId(userId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        return problemMapper.toResponseDto(problem);
    }

    public ProblemConstrainsResponseDto getProblemConstraints(Integer contestId, Integer problemId) {
        if (contestId == null) {
            Problem problem = problemRepository.findByIdAndIsPublicTrue(problemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
            return problemMapper.toProblemConstraintsDto(problem);
        }
        ContestProblem contestProblem = contestProblemRepository.findByContestIdAndProblemId(contestId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem in the contest not found"));
        ProblemConstrainsResponseDto response = problemMapper.toProblemConstraintsDto(contestProblem.getProblem());
        response.setContestId(contestProblem.getContest().getId());
        response.setContestEndTime(contestProblem.getContest().getEndTime());
        response.setContestStartTime(contestProblem.getContest().getStartTime());

        return response;
    }

    private Problem getOwnedProblem(Integer problemId, Integer userId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        if (problem.getCreatorId() != userId) {
            throw new ForbiddenException("Only author can manage problem");
        }
        return problem;
    }
}
