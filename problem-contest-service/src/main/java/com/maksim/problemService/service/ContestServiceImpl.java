package com.maksim.problemService.service;

import com.maksim.common.dto.PageResponseDto;
import com.maksim.problemService.dto.contest.ContestResponseDto;
import com.maksim.problemService.dto.contest.CreateContestDto;
import com.maksim.problemService.dto.contest.UpdateContestDto;
import com.maksim.problemService.dto.mapper.ContestMapper;
import com.maksim.problemService.dto.mapper.ProblemMapper;
import com.maksim.problemService.dto.problem.ProblemResponseDto;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.entity.*;
import com.maksim.problemService.entity.associative.ContestProblem;
import com.maksim.problemService.entity.associative.ContestUser;
import com.maksim.problemService.entity.keys.ContestUserId;
import com.maksim.problemService.exception.ConflictException;
import com.maksim.problemService.exception.ResourceNotFoundException;
import com.maksim.problemService.exception.ForbiddenException;
import com.maksim.problemService.exception.BadRequestException;
import com.maksim.problemService.repository.ContestRepository;
import com.maksim.problemService.repository.associative.ContestProblemRepository;
import com.maksim.problemService.repository.associative.ContestUserRepository;
import com.maksim.problemService.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestServiceImpl implements ContestService {

    private final ProblemRepository problemRepository;

    private final ContestRepository contestRepository;

    private final ContestUserRepository contestUserRepository;

    private final ContestMapper contestMapper;

    private final ProblemMapper problemMapper;

    private final ContestUserRepository cuRepository;

    private final ContestProblemRepository contestProblemRepository;

    @Transactional
    public ContestResponseDto createContest(CreateContestDto dto, Integer userId) {
        Contest contest = contestMapper.toEntity(dto);
        contest.setAuthorId(userId);
        assignProblems(contest, dto.problemsId(), userId);
        ContestResponseDto response = contestMapper.toResponseDto(contest);
        response.setProblems(getProblemSignatures(contest));
        contestRepository.save(contest);
        return response;
    }

    @Transactional
    public ContestResponseDto updateContest(Integer contestId, UpdateContestDto dto, Integer userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        if (contest.getAuthorId() != (int) userId) {
            throw new ForbiddenException("Only author can update contest");
        }
        if (contest.getStartTime().isBefore(Instant.now())) {
            throw new BadRequestException("Contest already started. You can't change it");
        }
        contestMapper.updateFromPatch(contest, dto);

        if (dto.problemsId() != null) {
            assignProblems(contest, dto.problemsId(), userId);
        }
        ContestResponseDto response = contestMapper.toResponseDto(contest);
        response.setProblems(getProblemSignatures(contest));
        contestRepository.save(contest);
        return response;
    }

    public void deleteContest(Integer contestId, Integer userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        if (contest.getAuthorId() != (int) userId) {
            throw new ForbiddenException("Only author can delete contest");
        }
        contestRepository.delete(contest);
    }

    @Transactional
    public ContestResponseDto getContestById(Integer contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));

        ContestResponseDto dto = contestMapper.toResponseDto(contest);

        if (Instant.now().isAfter(contest.getStartTime())) {
            dto.setProblems(getProblemSignatures(contest));
        }
        return dto;
    }

    @Transactional
    public PageResponseDto<ContestResponseDto> getPublicContests(Integer page, Integer pageSize) {
        Page<Contest> contests = contestRepository.findAllByOrderByStartTimeDesc(PageRequest.of(page - 1, pageSize));
        return buildResponsePage(contests);
    }

    @Transactional
    public PageResponseDto<ContestResponseDto> getUsersContests(Integer userId, Integer page, Integer pageSize) {
        Page<Contest> contests = contestUserRepository.findContestsByUserId(userId, PageRequest.of(page - 1, pageSize));
        return buildResponsePage(contests);
    }

    private PageResponseDto<ContestResponseDto> buildResponsePage(Page<Contest> contests) {
        Page<ContestResponseDto> response = contests.map(contest -> {
            ContestResponseDto dto = contestMapper.toResponseDto(contest);
            if (Instant.now().isAfter(contest.getStartTime())) {
                dto.setProblems(getProblemSignatures(contest));
            }
            return dto;
        });
        return PageResponseDto.from(response);
    }


    public ProblemResponseDto getProblem(Integer contestId, Integer problemId) {
        ContestProblem cp = contestProblemRepository.findByContestIdAndProblemId(contestId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found in contest"));
        if (cp.getContest().getStartTime().isAfter(Instant.now())) {
            throw new ConflictException("Contest has not started");
        }
        return problemMapper.toResponseDto(cp.getProblem());
    }

    @Transactional
    public void registerUser(Integer contestId, Integer userId) {
        if (!contestRepository.existsById(contestId))
            throw new ResourceNotFoundException("Contest not found");


        ContestUserId contestUserId = new ContestUserId(userId, contestId);
        if (contestUserRepository.existsById(contestUserId))
            throw new ConflictException("User is already registered");

        ContestUser cu = new ContestUser();
        cu.setId(contestUserId);
        cu.setContest(contestRepository.getReferenceById(contestId));
        cuRepository.save(cu);
    }

    public PageResponseDto<Integer> getRegisteredUsersIds(Integer contestId, Integer page, Integer pageSize) {
        Page<Integer> ids = contestUserRepository.findById_ContestId(contestId, PageRequest.of(page, pageSize))
                .map(contestUser -> contestUser.getId().getUserId());
        return PageResponseDto.from(ids);
    }

    private void assignProblems(Contest contest, List<Integer> problemIds, int userId) {
        if (problemIds == null || problemIds.isEmpty())
            throw new BadRequestException("Contest must has at least 1 problem");

        List<Integer> userProblemIds = problemIds.stream().distinct().toList();
        List<Problem> dbUserProblemIds = problemRepository
                .findProblemsByAuthorAndIds(userId, userProblemIds);

        if (userProblemIds.size() != dbUserProblemIds.size()) {
            Set<Integer> dbIds = dbUserProblemIds.stream().map(Problem::getId).collect(Collectors.toSet());
            List<Integer> badIds = userProblemIds.stream().filter(id -> !dbIds.contains(id)).toList();
            throw new BadRequestException("Some problems not found or do not belong to you: " + badIds);
        }

        contest.getProblems().clear();
        contest.getProblems().addAll((userProblemIds.stream()
                .map(problemId -> {
                    ContestProblem cp = new ContestProblem();
                    cp.setProblem(problemRepository.getReferenceById(problemId));
                    cp.setContest(contest);
                    return cp;
                }).toList()));
    }

    private List<ProblemSignatureResponseDto> getProblemSignatures(Contest contest) {
        return contest.getProblemList().stream()
                .map(problemMapper::toProblemSignature)
                .toList();
    }

}
