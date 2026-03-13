package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.dto.*;
import com.maksim.submissionAcceptorService.dto.mapper.SubmissionMapper;
import com.maksim.submissionAcceptorService.enums.ProgrammingLanguage;
import com.maksim.submissionAcceptorService.enums.Status;
import com.maksim.submissionAcceptorService.entity.Submission;
import com.maksim.submissionAcceptorService.event.SolutionJudgedEvent;
import com.maksim.submissionAcceptorService.exception.ResourceNotFoundException;
import com.maksim.submissionAcceptorService.exception.UnauthorizedAccessException;
import com.maksim.submissionAcceptorService.exception.ValidationException;
import com.maksim.submissionAcceptorService.repository.SubmissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubmissionService {

    @Value("${solution.submitted.event.topic}")
    private String solutionSubmittedTopicName;

    @Value("${standings.update.event.topic}")
    private String standingsUpdateTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final SubmissionRepository submissionRepository;

    private final AuthServiceClient authServiceClient;

    private final ProblemServiceClient problemServiceClient;

    private final SubmissionMapper submissionMapper;

    private static int PAGE_SIZE = 16;


    @Transactional
    public long submitSolution(int problemId, Integer contestId, int userId, CreateSubmissionDto solution) throws IOException, ExecutionException, InterruptedException {
        var submissionTime = LocalDateTime.now();
        ProblemConstraintsResponseDto constraints = problemServiceClient.getProblemConstraints(problemId, contestId);

        boolean isUpsolving = false;
        if (contestId != null) {
            if (submissionTime.isBefore(constraints.getContestStartTime()))
                throw new RuntimeException("The contest has not started");
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
        sendSubmissionEvent(submission, constraints, contestId);
        return submission.getId();
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

    public void sendSubmissionEvent(Submission s, ProblemConstraintsResponseDto p, Integer contestId) throws ExecutionException, InterruptedException {
        var event = submissionMapper.toSolutionSubmittedEvent(s);
        event.setCompilationTimeLimit(p.getCompileTimeLimit());
        event.setTimeLimit(p.getTimeLimit());
        event.setMemoryLimit(p.getMemoryLimit());
        event.setContestId(contestId);
        var record = new ProducerRecord<>(solutionSubmittedTopicName, UUID.randomUUID().toString(), (Object) event);
        record.headers().add("event-id", UUID.randomUUID().toString().getBytes());
        kafkaTemplate.send(record).get();
    }

    @Transactional
    public void processJudgedSolution(SolutionJudgedEvent ev) {
        Submission submission = submissionRepository.findById(ev.getSubmissionId()).
                orElseThrow(() -> new ResourceNotFoundException("Can't process event"));

        if (ev.getStatus() == submission.getStatus()){
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
            kafkaTemplate.send(standingsUpdateTopicName, event);
        }
    }


    // без транзакции
    public SubmissionDetailsResponseDto getSubmissionDetails(Long submissionId, Integer contestId, Integer userId) {
        var submission = submissionRepository.findByIdAndContestId(submissionId, contestId)
                .orElseThrow(() -> new ResourceNotFoundException("No submission found"));

        if (contestId != null && userId != submission.getUserId())
            throw new UnauthorizedAccessException("You can't get access to someone else's contest submission details");
        // аналогично. проверить на редис.
        return submissionMapper.toSubmissionDetailsResponseDto(submission);
    }



    // без транзакции
    public Page<SubmissionResponseDto> getSubmissions(Integer contestId, Integer problemId, Integer userId, Status status, ProgrammingLanguage language, Integer page) {
        return submissionRepository.findFiltered(contestId, problemId, userId, status, language, PageRequest.of(page - 1, PAGE_SIZE))
                .map(submissionMapper::toSubmissionResponseDto);
        // если статус IN QUEUE, то смотрим есть ли в редисе обнова для этой посылки
    }

    public void updateSolutionStatus(SolutionJudgedEvent solutionEvent) {
        // пушим в редис
        // пушим в сокет
    }
}
