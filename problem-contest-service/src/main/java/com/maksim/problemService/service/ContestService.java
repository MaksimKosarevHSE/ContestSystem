package com.maksim.problemService.service;

import com.maksim.problemService.dto.contest.ContestResponseDto;
import com.maksim.problemService.dto.contest.ContestSignatureResponseDto;
import com.maksim.problemService.dto.contest.CreateContestDto;
import com.maksim.problemService.dto.contest.UpdateContestDto;
import com.maksim.problemService.dto.mapper.ContestMapper;
import com.maksim.problemService.dto.mapper.ProblemMapper;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.entity.*;
import com.maksim.problemService.entity.associative.ContestProblem;
import com.maksim.problemService.entity.associative.ContestUser;
import com.maksim.problemService.entity.keys.ContestUserId;
import com.maksim.problemService.exception.ConflictException;
import com.maksim.problemService.exception.ResourceNotFoundException;
import com.maksim.problemService.exception.UnauthorizedAccessException;
import com.maksim.problemService.exception.ValidationException;
import com.maksim.problemService.repository.ContestRepository;
import com.maksim.problemService.repository.associative.ContestUserRepository;
import com.maksim.problemService.repository.ProblemRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class ContestService {
    private final ProblemRepository problemRepository;

    private final ContestRepository contestRepository;

    private final ContestMapper contestMapper;

    private final AuthServiceClient authServiceClient;

    private final ProblemMapper problemMapper;

    private final ContestUserRepository cuRepository;

    @Transactional
    public Integer createContest(CreateContestDto dto, int userId) {
        Contest contest = contestMapper.toEntity(dto);
        contest.setAuthorId(userId);
        assignProblems(contest, dto.getProblemsId(), userId);
        return contestRepository.save(contest).getId();
    }

    @Transactional
    public void deleteContest(int contestId, int userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        if (contest.getAuthorId() != userId) {
            throw new UnauthorizedAccessException("Only author can delete contest");
        }
        contestRepository.delete(contest);
    }

    @Transactional
    public void updateContest(int contestId, UpdateContestDto dto, int userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        if (contest.getAuthorId() != userId) {
            throw new UnauthorizedAccessException("Only author can update contest");
        }
        if (contest.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Contest already started. You can't change it");
        }
        contestMapper.updateEntity(contest, dto);
        assignProblems(contest, dto.getProblemsId(), userId);
        contestRepository.save(contest);
    }


    public List<ProblemSignatureResponseDto> getAllProblemSignatures(Integer contestId) {
        var contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("No contest found with id " + contestId));
        return contest.getProblems().stream().map(problemMapper::toProblemSignature).toList();
    }

    public Problem getProblem(Integer contestId, Integer problemId) {
        return contestRepository.getProblem(contestId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException("No problem found with id " + problemId));
    }

    public ContestResponseDto getContestById(int contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        ContestResponseDto dto = contestMapper.toResponseDto(contest);

        List<ProblemSignatureResponseDto> problems = contest.getProblems().stream()
                .map(problemMapper::toProblemSignature)
                .toList();
        dto.setProblems(problems);
        return dto;
    }


    public Page<ContestSignatureResponseDto> getPublicContests(Integer page, Integer pageSize) {
        Page<ContestSignatureResponseDto> dto = contestRepository.getAll(PageRequest.of(page - 1, pageSize));
        setHandles(dto.getContent());
        return dto;
    }


    public Page<ContestSignatureResponseDto> getUserContests(int userId, Integer page, Integer pageSize) {
        Page<ContestSignatureResponseDto> dtoList = contestRepository.getUserContests(userId, PageRequest.of(page - 1, pageSize));
        setHandles(dtoList.getContent());
        return dtoList;
    }

    private void setHandles(List<ContestSignatureResponseDto> contests) {
        List<Integer> authorIds = contests.stream()
                .map(ContestSignatureResponseDto::getAuthorId)
                .distinct()
                .toList();
        Map<Integer, String> handles = authServiceClient.getUsersHandles(authorIds);
        contests.forEach(dto -> dto.setAuthorHandle(handles.get(dto.getAuthorId())));
    }


    public ProblemConstraintsResponseDto getConstraints(Integer contestId, Integer problemId) {
        return contestRepository.getProblemConstraints(contestId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException("No problem found with id " + problemId));
    }

    public void registerUser(Integer contestId, Integer userId) {
        if (!contestRepository.existsById(contestId))
            throw new ResourceNotFoundException("There is no contest with id " + contestId);

        var cuDb = cuRepository.findById_ContestIdAndId_UserId(contestId, userId);

        if (cuDb.isPresent())
            throw new ConflictException("User already registered");

        ContestUser cu = new ContestUser();
        cu.setId(new ContestUserId(userId, contestId));
        cu.setContest(contestRepository.getReferenceById(contestId));
        cuRepository.save(cu);
    }

    private void assignProblems(Contest contest, List<Integer> problemIds, int userId) {
        List<Integer> distinctProblemsId = problemIds.stream().distinct().toList();
        List<Problem> userProblemIntersection = contestRepository.getAuthorProblemsList(userId, distinctProblemsId);

        if (userProblemIntersection.size() != distinctProblemsId.size()) {
            // ограничения маленькие, О(n^2)
            Problem tmp = new Problem();
            StringBuilder message = new StringBuilder("Some problems are not found or do not belong you. Incorrect ids: [");
            for (var taskId : distinctProblemsId) {
                tmp.setId(taskId);
                if (!userProblemIntersection.contains(tmp)) message.append(taskId).append(", ");
            }
            message.setLength(message.length() - 2);
            message.append("]");
            throw new ValidationException(message.toString());
        }

        contest.setProblems(distinctProblemsId.stream()
                .map(problemId -> {
                    Problem problem = problemRepository.getReferenceById(problemId);
                    ContestProblem cp = new ContestProblem();
                    cp.setContest(contest);
                    cp.setProblem(problem);
                    return cp;
                }).toList());
    }
}
