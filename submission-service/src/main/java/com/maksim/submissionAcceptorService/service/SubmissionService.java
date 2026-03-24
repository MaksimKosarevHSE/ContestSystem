package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.dto.*;
import com.maksim.submissionAcceptorService.dto.mapper.SubmissionMapper;
import com.maksim.submissionAcceptorService.enums.ProgrammingLanguage;
import com.maksim.submissionAcceptorService.enums.Status;
import com.maksim.submissionAcceptorService.entity.Submission;
import com.maksim.submissionAcceptorService.kafka.event.SolutionSubmittedEvent;
import com.maksim.submissionAcceptorService.kafka.event.SubmissionJudgingProgressEvent;
import com.maksim.submissionAcceptorService.exception.ResourceNotFoundException;
import com.maksim.submissionAcceptorService.exception.UnauthorizedAccessException;
import com.maksim.submissionAcceptorService.exception.ValidationException;
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
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionService {

    @Value("${solution.submitted.event.topic}")
    private String solutionSubmittedTopicName;

    @Value("${standings.update.event.topic}")
    private String standingsUpdateTopicName;

    private final SubmissionRepository submissionRepository;

    private final ProblemServiceClient problemServiceClient;

    private final JudgingProgressCacheService submissionProgressCacheService;

    private final SubmissionMapper submissionMapper;

    private final OutboxEventService outboxEventService;

    private static final int PAGE_SIZE = 16;

    @Transactional
    public long submitSolution(int problemId, Integer contestId, int userId, CreateSubmissionDto solution) {
        try {
            LocalDateTime submissionTime = LocalDateTime.now();
            ProblemConstraintsResponseDto constraints = problemServiceClient.getProblemConstraints(problemId, contestId);
            boolean isUpsolving = false;
            if (contestId != null) {
                if (submissionTime.isBefore(constraints.getContestStartTime())) {
                    throw new RuntimeException("The contest has not started");
                }
                if (submissionTime.isAfter(constraints.getContestEndTime()))
                    isUpsolving = true;
            }
            String source = extractSource(solution);

            Submission submission = Submission.builder()
                    .userId(userId)
                    .problemId(problemId)
                    .contestId(contestId)
                    .time(submissionTime)
                    .source(source)
                    .programmingLanguage(solution.getLanguage())
                    .status(Status.IN_QUEUE)
                    .isUpsolving(isUpsolving).build();

            submission = submissionRepository.save(submission);

            SolutionSubmittedEvent event = submissionMapper.toSolutionSubmittedEvent(submission);
            event.setCompilationTimeLimit(constraints.getCompileTimeLimit());
            event.setTimeLimit(constraints.getTimeLimit());
            event.setMemoryLimit(constraints.getMemoryLimit());
            event.setContestId(contestId);

            outboxEventService.save(solutionSubmittedTopicName, event);

            return submission.getId();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractSource(CreateSubmissionDto solution) throws IOException {
        boolean hasSourceFile = solution.getSourceFile() != null;
        boolean hasSourceCode = solution.getSourceCode() != null && !solution.getSourceCode().isBlank();
        if (!hasSourceFile && !hasSourceCode)
            throw new ValidationException("Source code was not provided");
        if (hasSourceFile) {
            return new String(solution.getSourceFile().getBytes());
        }
        return solution.getSourceCode();
    }


    @Transactional
    public void processJudgedSolution(SubmissionJudgingProgressEvent ev) {
        Submission submission = submissionRepository.findById(ev.getSubmissionId()).
                orElseThrow(() -> new ResourceNotFoundException("Can't process event"));

        if (ev.getStatus() == submission.getStatus()) {
            return; // идемпотентность
        }

        submission.setStatus(ev.getStatus());
        submission.setExecutionTime(ev.getExecutionTime());
        submission.setTestNum(ev.getTestNum());
        submission.setUsedMemory(ev.getMemory());
        submission.setCheckerMessage(ev.getCheckerMessage());

        submissionRepository.save(submission);

        if (submission.getContestId() != null && !submission.getIsUpsolving()) {
            var event = submissionMapper.toStandingsUpdateEvent(submission);
            outboxEventService.save(standingsUpdateTopicName, event);
        }
    }


    public SubmissionDetailsResponseDto getSubmissionDetails(Long submissionId, Integer contestId, Integer userId) {
        var submission = submissionRepository.findByIdAndContestId(submissionId, contestId)
                .orElseThrow(() -> new ResourceNotFoundException("No submission found"));

        if (contestId != null && userId != submission.getUserId())
            throw new UnauthorizedAccessException("You can't get access to someone else's contest submission details");
        return submissionMapper.toSubmissionDetailsResponseDto(submission);
    }


    // без транзакции
    public Page<SubmissionResponseDto> getSubmissions(Integer contestId, Integer problemId, Integer userId, Status status, ProgrammingLanguage language, Integer page) {
        return submissionRepository.findFiltered(contestId, problemId, userId, status, language, PageRequest.of(page - 1, PAGE_SIZE))
                .map(this::wrapWithJudgingProgressTestNum);
    }

    private SubmissionResponseDto wrapWithJudgingProgressTestNum(Submission submission) {
        SubmissionResponseDto response = submissionMapper.toSubmissionResponseDto(submission);
        if (response.getStatus() == Status.IN_QUEUE)
            response.setTestNum(submissionProgressCacheService.getCachedTestNum(submission.getId()).orElse(null));
        return response;
    }

}
