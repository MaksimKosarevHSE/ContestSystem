package com.maksim.problemService.service;

import com.maksim.problemService.dto.contest.ContestSignatureDto;
import com.maksim.problemService.dto.contest.CreateContestDto;
import com.maksim.problemService.dto.problem.ProblemSignature;
import com.maksim.problemService.entity.Contest;
import com.maksim.problemService.entity.ContestProblem;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.entity.ProblemConstraints;
import com.maksim.problemService.repository.ContestRepository;
import com.maksim.problemService.repository.ProblemRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

@Service
@Transactional
public class ContestService {
    private final ProblemRepository problemRepository;

    private final ContestRepository contestRepository;

    public ContestService(ProblemRepository problemRepository, ContestRepository contestRepository) {
        this.problemRepository = problemRepository;
        this.contestRepository = contestRepository;
    }


    public List<ProblemSignature> getAllProblemSignatures(Integer contestId) {
        var contest = contestRepository.findById(contestId).orElseThrow(() -> new RuntimeException("No contest with id"));
        var problems = contest.getProblems();
        List<ProblemSignature> list = new ArrayList<>(problems.size());
        for (var el : problems) {
            list.add(new ProblemSignature(el.getId(), el.getTitle(), el.getComplexity()));
        }
        return list;
    }

    public Problem getProblem(Integer contestId, Integer problemId) {
        return contestRepository.getProblem(contestId, problemId).orElseThrow(() -> new RuntimeException("No such problem with in contest"));
    }

    public List<ContestSignatureDto> getPublicContests(Integer page, Integer pageSize) {
        return contestRepository.getAll(PageRequest.of(page, pageSize));
    }

    public List<ContestSignatureDto> getUserContests(int userId, Integer page, Integer pageSize) {
        return contestRepository.getUserContests(userId, PageRequest.of(page, pageSize));
    }

    public int createContest(CreateContestDto dto, int userId) {
        Contest contest = new Contest();
        contest.setAuthorId(userId);
        contest.setStartTime(dto.getStartTime());
        contest.setEndTime(dto.getEndTime());
        contest.setTitle(dto.getTitle());

        var uniqueIds = validateProblemsList(dto.getProblemsId(), userId);
        var problemContest = new LinkedList<ContestProblem>();
        for (var problemId : uniqueIds) {
            var c_p = new ContestProblem();
            c_p.setContest(contest);
            c_p.setProblem(problemRepository.getReferenceById(problemId));
            problemContest.add(c_p);
        }
        contest.setProblems(problemContest);
        return contestRepository.save(contest).getId();
    }

    public List<Integer> validateProblemsList(List<Integer> list, int authorId) {
        var uniqueIds = new ArrayList<Integer>(new TreeSet<Integer>(list));
        int cntInDb = contestRepository.getAuthorProblemsListCount(authorId, uniqueIds);
        if (cntInDb != uniqueIds.size()) {
            throw new RuntimeException("Некоторые задачи не найдены");
        }
        return uniqueIds;
    }

    public ProblemConstraints getConstraints(Integer contestId, Integer problemId) {
        return contestRepository.getProblemConstraints(contestId, problemId).orElseThrow(() -> new RuntimeException("No such problem with in contest"));
    }
}
