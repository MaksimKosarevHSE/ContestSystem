package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.dto.*;
import com.maksim.submissionAcceptorService.enums.Status;
import com.maksim.submissionAcceptorService.entity.Submission;
import com.maksim.submissionAcceptorService.event.SolutionJudgedEvent;
import com.maksim.submissionAcceptorService.event.SolutionSubmittedEvent;
import com.maksim.submissionAcceptorService.event.StandingsUpdateEvent;
import com.maksim.submissionAcceptorService.repository.SubmissionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
@Slf4j
public class SubmissionService {
    @Value("${problem.service.url}")
    private String PROBLEM_SERVICE_URL;

    @Value("${solution.submitted.event.topic}")
    private String TOPIC_NAME;

    private KafkaTemplate<String, Object> kafkaTemplate;

    private SubmissionRepository submissionRepository;

    private RestTemplate restTemplate;

    private int PAGE_SIZE = 16;

    public SubmissionService(KafkaTemplate<String, Object> kafkaTemplate,
                             SubmissionRepository submitSolutionRepository,
                             RestTemplate rest) {
        this.kafkaTemplate = kafkaTemplate;
        this.submissionRepository = submitSolutionRepository;
        this.restTemplate = rest;
    }

    public void validateContestSubmission(ProblemConstraintsResponseDto constraints, LocalDateTime submissionTime) {
        if (submissionTime.isBefore(constraints.getContestStartTime()))
            throw new RuntimeException("The contest has not started");
        if (submissionTime.isAfter(constraints.getContestEndTime()))
            throw new RuntimeException("The contest is finished");
    }


    public long submitSolution(int problemId, Integer contestId, int userId, CreateSubmissionDto solution) throws IOException, ExecutionException, InterruptedException {
        var submissionTime = LocalDateTime.now();
        ProblemConstraintsResponseDto problem = getProblemConstraints(problemId, contestId);
        if (problem == null)
            throw new RuntimeException("No problem found with id " + problemId);
        if (contestId != null) {
            validateContestSubmission(problem, submissionTime);
        }

        String source = extractSource(solution);
        Submission submission = new Submission(userId, problemId, contestId, submissionTime, source, solution.getLanguage(), Status.IN_QUEUE, 0, 0, 0);
        submission = submissionRepository.save(submission);
        sendSubmissionEvent(submission, problem, contestId);
        return submission.getId();
    }


    public Page<SubmissionResponseDto> getSuccessPracticeSubmissions(Integer problemId, Integer page) {
        return submissionRepository.getSubmissionsByProblemIdAndStatus(problemId, null, Status.OK, PageRequest.of(page, PAGE_SIZE));
    }


    public Page<SubmissionResponseDto> getAllUserPracticeSubmissions(Integer userId, Integer contestId, Integer pageNum) {
        return submissionRepository.getAllSubmissionsByUserId(userId, contestId, PageRequest.of(pageNum, PAGE_SIZE));
    }


    public Page<SubmissionResponseDto> getSubmissions(Integer userId, Integer problemId, Integer contestId, Integer pageNum) {
        return submissionRepository.getSubmissionByUserIdAndProblemIdAndContestId(userId, problemId, contestId, PageRequest.of(pageNum, PAGE_SIZE));
    }

    public SubmissionDetailsResponseDto getSubmissionDetails(Long submissionId, int userId) {
        // TODO проверить есть ли у пользователя права на просмотр
        var submission = submissionRepository.findById(submissionId).orElseThrow(() -> new RuntimeException("No submission found with id " + submissionId));
        return new ObjectMapper().convertValue(submission, SubmissionDetailsResponseDto.class);
    }


    private String extractSource(CreateSubmissionDto solution) throws IOException {
        boolean hasSourceFile = solution.getSourceFile() != null;
        boolean hasSourceCode = solution.getSourceCode() != null && !solution.getSourceCode().isBlank();
        if (!hasSourceFile && !hasSourceCode)
            throw new RuntimeException("Source code was not provided");
        if (hasSourceFile) {
            return new String(solution.getSourceFile().getBytes());
        }
        return solution.getSourceCode();
    }


    private ProblemConstraintsResponseDto getProblemConstraints(int problemId, Integer contestId) {
        String address = MessageFormat.format("{0}/problem/{1}/constraints", PROBLEM_SERVICE_URL, problemId);
        if (contestId != null)
            address = MessageFormat.format("{0}/contest/{1}/problem/{2}/constraints", PROBLEM_SERVICE_URL, contestId, problemId);

        ResponseEntity<ProblemConstraintsResponseDto> response = restTemplate.getForEntity(address,
                ProblemConstraintsResponseDto.class, problemId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }


    public void sendSubmissionEvent(Submission s, ProblemConstraintsResponseDto p, Integer contestId) throws ExecutionException, InterruptedException {
        var event = new SolutionSubmittedEvent(
                s.getProblemId(),
                contestId,
                s.getUserId(),
                s.getId(),
                s.getSource(),
                s.getProgrammingLanguage(),
                p.getTimeLimit(),
                p.getMemoryLimit(),
                p.getCompileTimeLimit());

        var record = new ProducerRecord<>(TOPIC_NAME, UUID.randomUUID().toString(), (Object) event);
        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        var sendResult = kafkaTemplate.send(record).get();
        log.error("Message has been sent to topic {} in partitions {}", TOPIC_NAME, sendResult.getRecordMetadata().partition());
    }


    public void saveVerdict(SolutionJudgedEvent ev) {
        Submission submission = submissionRepository.findById(ev.getSubmissionId()).get();
        submission.setStatus(ev.getStatus());
        submission.setExecutionTime(ev.getExecutionTime());
        submission.setTestNum(ev.getTestNum());
        submission.setUsedMemory(ev.getMemory());
        if (submission.getContestId() != null && !submission.getIsUpsolving()) {
            var event = new StandingsUpdateEvent(submission.getUserId(), submission.getContestId(), submission.getProblemId(), submission.getTime(), submission.getStatus());
            kafkaTemplate.send("standings-update-event", event);
        }
    }


}
