package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.dto.ProblemDto;
import com.maksim.submissionAcceptorService.dto.SubmitSolutionTextDto;
import com.maksim.submissionAcceptorService.entity.Submission;
import com.maksim.submissionAcceptorService.repository.SubmitSolutionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
@Slf4j
public class SubmitSolutionService {
    @Value("${problem.service.url}")
    private String PROBLEM_SERVICE_URL;

    @Value("${submission.event.topic}")
    private String TOPIC_NAME;

    private KafkaTemplate<Integer, SolutionSubmittedEvent> kafkaTemplate;

    private SubmitSolutionRepository submitSolutionRepository;

    private RestTemplate restTemplate;

    public SubmitSolutionService(KafkaTemplate<Integer, SolutionSubmittedEvent> kafkaTemplate,
                                 SubmitSolutionRepository submitSolutionRepository,
                                 RestTemplate rest) {
        this.kafkaTemplate = kafkaTemplate;
        this.submitSolutionRepository = submitSolutionRepository;
        this.restTemplate = rest;
    }


    public long  createSubmission(SubmitSolutionTextDto solution, int userId) throws ExecutionException, InterruptedException {
        ProblemDto problem = getProblemConstraints(solution.getProblemId());
        if (problem == null)
            throw new RuntimeException("No problem found with id " + solution.getProblemId());

        Submission submission = new Submission(userId, problem.getId(), LocalDateTime.now(), solution.getSource(), solution.getLanguage());
        submission = submitSolutionRepository.save(submission);
        sendSubmissionEvent(submission, problem);

        return submission.getId();
    }

    private ProblemDto getProblemConstraints(int problemId) {
        ResponseEntity<ProblemDto> response = restTemplate.getForEntity("http://"+PROBLEM_SERVICE_URL + "/problem/{id}/constraints",
                ProblemDto.class, problemId);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    private void sendSubmissionEvent(Submission s, ProblemDto p) throws ExecutionException, InterruptedException {
        var event = new SolutionSubmittedEvent(
                s.getProblemId(),
                s.getUserId(),
                s.getId(),
                s.getSource(),
                s.getProgrammingLanguage(),
                p.getTimeLimit(),
                p.getMemoryLimit(),
                p.getCompileTimeLimit());

        var record = new ProducerRecord<>(TOPIC_NAME, s.getUserId(), event);
        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        var sendResult = kafkaTemplate.send(record).get();
        log.debug("Message has been sent to topic {} in partitions {}", TOPIC_NAME, sendResult.getRecordMetadata().partition());
    }


}
