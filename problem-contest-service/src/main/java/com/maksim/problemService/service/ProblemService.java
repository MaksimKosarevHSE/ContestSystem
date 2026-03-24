package com.maksim.problemService.service;


import com.maksim.problemService.client.JudgingServiceClient;
import com.maksim.problemService.dto.mapper.ProblemMapper;
import com.maksim.problemService.dto.problem.ProblemCreateDto;
import com.maksim.problemService.dto.problem.ProblemResponseDto;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.dto.problem.ProblemUpdateDto;
import com.maksim.problemService.dto.problem.SendTestCasesToJudgeServiceDto;
import com.maksim.problemService.exception.ResourceNotFoundException;
import com.maksim.problemService.exception.UnauthorizedAccessException;
import com.maksim.problemService.exception.ValidationException;
import com.maksim.problemService.repository.associative.ContestProblemRepository;
import com.maksim.problemService.validators.ProblemValidator;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.repository.ProblemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemRepository problemRepository;

    private final ProblemValidator problemValidator;

    private final ContestProblemRepository contestProblemRepository;

    private final JudgingServiceClient judgingServiceClient;

    private final ProblemMapper problemMapper;


    @Transactional
    public ProblemResponseDto createProblem(ProblemCreateDto problemCreateDto, int creatorId) {
        problemValidator.validate(problemCreateDto);
        Problem problem = problemMapper.toEntity(problemCreateDto);
        problem.setCreatorId(creatorId);
        problem = problemRepository.save(problem);

        SendTestCasesToJudgeServiceDto saveTestsDto = SendTestCasesToJudgeServiceDto.from(problemCreateDto);
        saveTestsDto.setProblemId(problem.getId());
        judgingServiceClient.saveTestCases(saveTestsDto);

        return problemMapper.toResponseDto(problem);
    }

    public ProblemResponseDto updateProblem(int id, ProblemUpdateDto dto, int userId) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        if (problem.getCreatorId() != userId) {
            throw new UnauthorizedAccessException("Only author can update problem");
        }
        problemMapper.updateFromPatch(problem, dto);
        problem = problemRepository.save(problem);
        return problemMapper.toResponseDto(problem);
    }

    public void deleteProblem(int id, int userId) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        if (problem.getCreatorId() != userId) {
            throw new UnauthorizedAccessException("Only author can delete problem");
        }
        if (contestProblemRepository.existsByProblemId(id)) {
            throw new ValidationException("Problem is used in contests, can't delete");
        }
        problemRepository.delete(problem);
    }

    public Page<ProblemSignatureResponseDto> getPublicProblemsSignatures(int pageNumber, int pageSize) {
        return problemRepository.findByIsPublicTrue(PageRequest.of(pageNumber - 1, pageSize))
                .map(problemMapper::toProblemSignature);
    }

    public Page<ProblemSignatureResponseDto> getUsersProblemsSignatures(Integer userId, int pageNumber, int pageSize) {
        return problemRepository.findByCreatorId(userId, PageRequest.of(pageNumber - 1, pageSize))
                .map(problemMapper::toProblemSignature);
    }

    public ProblemResponseDto getPublicProblemById(int id) {
        Problem problem = problemRepository.findByIdAndIsPublicTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        return problemMapper.toResponseDto(problem);
    }

    public ProblemResponseDto getUsersProblem(Integer userId, Integer problemId) {
        Problem problem = problemRepository.findByCreatorIdAndId(userId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        return problemMapper.toResponseDto(problem);
    }
}



