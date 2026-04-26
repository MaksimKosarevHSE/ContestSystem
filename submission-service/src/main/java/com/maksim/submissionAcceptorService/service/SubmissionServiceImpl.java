package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.client.ProblemServiceClient;
import com.maksim.submissionAcceptorService.dto.PageResponseDto;
import com.maksim.submissionAcceptorService.dto.mapper.SubmissionMapper;
import com.maksim.submissionAcceptorService.dto.problem.ProblemConstrainsResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionCreateDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionDetailsResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionResponseDto;
import com.maksim.submissionAcceptorService.enums.Status;
import com.maksim.submissionAcceptorService.entity.Submission;
import com.maksim.submissionAcceptorService.event.SolutionSubmittedEvent;
import com.maksim.submissionAcceptorService.event.SolutionJudgedEvent;
import com.maksim.submissionAcceptorService.exception.ResourceNotFoundException;
import com.maksim.submissionAcceptorService.exception.ForbiddenException;
import com.maksim.submissionAcceptorService.exception.BadRequestException;
import com.maksim.submissionAcceptorService.repository.SubmissionRepository;
import com.maksim.submissionAcceptorService.service.outbox.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import java.time.Instant;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    @Value("${solution.submitted.event.topic}")
    private String solutionSubmittedTopicName;

    @Value("${standings.update.event.topic}")
    private String standingsUpdateTopicName;

    private final SubmissionRepository submissionRepository;

    private final ProblemServiceClient problemServiceClient;

    private final JudgingProgressCacheService submissionProgressCacheService;

    private final SubmissionMapper submissionMapper;

    private final OutboxEventService outboxEventService;

    private static final int PAGE_SIZE = 20;

    @Transactional
    public SubmissionResponseDto submitSolution(Integer problemId, Integer contestId, Integer userId, SubmissionCreateDto solution) {
        Instant submissionTime = Instant.now();
        ProblemConstrainsResponseDto constraints = problemServiceClient.getProblemConstraints(problemId, contestId);

        boolean isUpsolving = contestId != null && submissionTime.isAfter(constraints.contestEndTime());
        String source = extractSource(solution);

        Submission submission = Submission.builder()
                .userId(userId)
                .problemId(problemId)
                .contestId(contestId)
                .time(submissionTime)
                .source(source)
                .programmingLanguage(solution.language())
                .status(Status.IN_QUEUE)
                .isUpsolving(isUpsolving).build();

        submission = submissionRepository.save(submission);

        SolutionSubmittedEvent event = submissionMapper.toSolutionSubmittedEvent(submission);
        submissionMapper.updateSolutionEventFromConstraints(event, constraints);

        outboxEventService.save(solutionSubmittedTopicName, event);
        return submissionMapper.toSubmissionResponseDto(submission);
    }


    public SubmissionResponseDto getSubmission(Long submissionId, Integer contestId) {
        Submission submission = submissionRepository.findByIdAndContestId(submissionId, contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        SubmissionResponseDto response = submissionMapper.toSubmissionResponseDto(submission);
        wrapWithJudgingProgress(response);
        return response;
    }


    public SubmissionDetailsResponseDto getSubmissionDetails(Long submissionId, Integer contestId, Integer userId) {
        Submission submission = submissionRepository.findByIdAndContestId(submissionId, contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        if (!Objects.equals(userId, submission.getUserId()))
            throw new ForbiddenException("You can't get access to someone else's submission details");

        SubmissionDetailsResponseDto detailsResponse = submissionMapper.toSubmissionDetailsResponseDto(submission);
        if (detailsResponse.getStatus() == Status.IN_QUEUE) {
            Integer testNum = submissionProgressCacheService.getCachedTestNum(submission.getId()).orElse(null);
            if (testNum != null) {
                detailsResponse.setTestNum(testNum);
                detailsResponse.setStatus(Status.TESTING);
            }
        }
        return detailsResponse;
    }


    public PageResponseDto<SubmissionResponseDto> getSubmissions(Integer contestId, Integer problemId, Integer userId, Status status, Integer page) {
        Page<SubmissionResponseDto> pageResponse = submissionRepository
                .findAllFiltered(contestId, problemId, userId, status, PageRequest.of(page - 1, PAGE_SIZE))
                .map(submission -> {
                    SubmissionResponseDto responseDto = submissionMapper.toSubmissionResponseDto(submission);
                    wrapWithJudgingProgress(responseDto);
                    return responseDto;
                });
        return PageResponseDto.from(pageResponse);
    }


    @Transactional
    public void processJudgedSolution(SolutionJudgedEvent ev) {
        Submission submission = submissionRepository.findById(ev.getSubmissionId()).
                orElseThrow(() -> new ResourceNotFoundException("Can't process event"));

        if (ev.getStatus() == submission.getStatus()) {
            return; // идемпотентность
        }

        submissionMapper.updateFromEvent(submission, ev);
        submissionRepository.save(submission);

        if (submission.getContestId() != null && !submission.getIsUpsolving()) {
            var event = submissionMapper.toStandingsUpdateEvent(submission);
            outboxEventService.save(standingsUpdateTopicName, event);
        }
    }


    private void wrapWithJudgingProgress(SubmissionResponseDto response) {
        if (response.getStatus() == Status.IN_QUEUE) {
            Integer testNum = submissionProgressCacheService.getCachedTestNum(response.getId()).orElse(null);
            if (testNum == null) return;
            response.setTestNum(testNum);
            response.setStatus(Status.TESTING);
        }
    }

    private String extractSource(SubmissionCreateDto solution) {
        try {
            boolean hasSourceFile = solution.sourceFile() != null;
            boolean hasSourceCode = solution.sourceCode() != null && !solution.sourceCode().isBlank();
            if (!hasSourceFile && !hasSourceCode)
                throw new BadRequestException("Source code was not provided");
            if (hasSourceFile) {
                return new String(solution.sourceFile().getBytes());
            }
            return solution.sourceCode();
        } catch (IOException ex) {
            throw new RuntimeException("Error while extracting source code");
        }
    }

}