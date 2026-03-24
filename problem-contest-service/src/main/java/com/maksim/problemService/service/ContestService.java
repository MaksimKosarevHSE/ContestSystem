package com.maksim.problemService.service;

import com.maksim.problemService.dto.contest.ContestSignatureResponseDto;
import com.maksim.problemService.dto.contest.CreateContestDto;
import com.maksim.problemService.dto.mapper.ContestMapper;
import com.maksim.problemService.dto.mapper.ProblemMapper;
import com.maksim.problemService.dto.problem.ProblemConstraints;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.entity.*;
import com.maksim.problemService.entity.associative.ContestProblem;
import com.maksim.problemService.entity.associative.ContestUser;
import com.maksim.problemService.entity.keys.ContestUserId;
import com.maksim.problemService.exception.ConflictException;
import com.maksim.problemService.exception.ResourceNotFoundException;
import com.maksim.problemService.exception.ValidationException;
import com.maksim.problemService.repository.ContestRepository;
import com.maksim.problemService.repository.ContestUserRepository;
import com.maksim.problemService.repository.ProblemRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

    public List<ProblemSignatureResponseDto> getAllProblemSignatures(Integer contestId) {
        var contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("No contest found with id " + contestId));
        return contest.getProblems().stream().map(problemMapper::toProblemSignature).toList();
    }

    public Problem getProblem(Integer contestId, Integer problemId) {
        return contestRepository.getProblem(contestId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException("No problem found with id " + problemId));
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


    public int createContest(CreateContestDto dto, int userId) {
        List<Integer> distinctProblemsId = dto.getProblemsId().stream().distinct().toList();
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

        dto.setProblemsId(distinctProblemsId);

        Contest contest = contestMapper.toEntity(dto);
        // доп внедрение id Задач
        contest.setProblems(distinctProblemsId.stream()
                .map(problemId -> {
                    Problem problem = problemRepository.getReferenceById(problemId);
                    ContestProblem cp = new ContestProblem();
                    cp.setContest(contest);
                    cp.setProblem(problem);
                    return cp;
                }).toList());

        contest.setAuthorId(userId);
        return contestRepository.save(contest).getId();
    }


    public ProblemConstraints getConstraints(Integer contestId, Integer problemId) {
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
}
