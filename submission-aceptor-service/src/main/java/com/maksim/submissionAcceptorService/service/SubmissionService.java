package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.dto.*;
import com.maksim.submissionAcceptorService.entity.Status;
import com.maksim.submissionAcceptorService.entity.Submission;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
@Slf4j
public class SubmissionService {
    @Value("${problem.service.url}")
    private String PROBLEM_SERVICE_URL;

    @Value("${submission.event.topic}")
    private String TOPIC_NAME;

    private KafkaTemplate<String, SolutionSubmittedEvent> kafkaTemplate;

    private SubmissionRepository submissionRepository;

    private RestTemplate restTemplate;

    private int PAGE_SIZE = 16;
    public SubmissionService(KafkaTemplate<String, SolutionSubmittedEvent> kafkaTemplate,
                             SubmissionRepository submitSolutionRepository,
                             RestTemplate rest) {
        this.kafkaTemplate = kafkaTemplate;
        this.submissionRepository = submitSolutionRepository;
        this.restTemplate = rest;
    }

    public long submitPracticeSolution(int problemId, int userId, SubmissionRequestDto solution) throws IOException, ExecutionException, InterruptedException {
        ProblemConstraintsDto problem = getProblemConstraints(problemId);
        if (problem == null)
            throw new RuntimeException("No problem found with id " + problemId);
        String source = extractSource(solution);
        Submission submission = new Submission(userId, problemId, LocalDateTime.now(), source, solution.getLanguage(), Status.IN_QUEUE, 0, 0, 0);
        submission = submissionRepository.save(submission);
        sendSubmissionEvent(submission, problem, null);
        return submission.getId();
    }


    public List<GetSubmissionDto> getAllSubmissions() {
        return submissionRepository.customFindAll();
    }

    public GetSubmissionDto getSubmission(long id) {
        Submission submission = submissionRepository.findById(id).orElseThrow(() -> new RuntimeException("No submission with id " + id + " found"));
        return new ObjectMapper().convertValue(submission, GetSubmissionDto.class);
    }


    private String extractSource(SubmissionRequestDto solution) throws IOException {
        boolean hasSourceFile = solution.getSourceFile() != null;
        boolean hasSourceCode = solution.getSourceCode() != null && !solution.getSourceCode().isBlank();
        if (!hasSourceFile && !hasSourceCode)
            throw new RuntimeException("Source code was not provided");
        if (hasSourceFile) {
            return new String(solution.getSourceFile().getBytes());
        }
        return solution.getSourceCode();
    }


    private ProblemConstraintsDto getProblemConstraints(int problemId) {
        ResponseEntity<ProblemConstraintsDto> response = restTemplate.getForEntity("http://" + PROBLEM_SERVICE_URL + "/problem/{id}/constraints",
                ProblemConstraintsDto.class, problemId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }


    public void sendSubmissionEvent(Submission s, ProblemConstraintsDto p, Integer contestId) throws ExecutionException, InterruptedException {
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

        var record = new ProducerRecord<>(TOPIC_NAME, UUID.randomUUID().toString(), event);
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
    }


    public Page<GetSubmissionDto> getUserSubmissions(Integer problemId, Integer userId, Integer pageNum) {
        return submissionRepository.getSubmissionByProblemIdAndUserId(problemId, userId, PageRequest.of(pageNum, PAGE_SIZE));

    }

    public Page<GetSubmissionDto> getSuccessPracticeSubmissions(Integer problemId, Integer page) {
        return submissionRepository.getSubmissionByProblemIdAndStatus(problemId, PageRequest.of(page, PAGE_SIZE));
    }
}
