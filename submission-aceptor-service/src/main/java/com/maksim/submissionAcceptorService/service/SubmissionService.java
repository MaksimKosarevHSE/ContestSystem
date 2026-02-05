package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.dto.GetSubmissionDto;
import com.maksim.submissionAcceptorService.dto.ProblemDto;
import com.maksim.submissionAcceptorService.dto.SolutionJudgedEvent;
import com.maksim.submissionAcceptorService.dto.SubmitSolutionTextDto;
import com.maksim.submissionAcceptorService.entity.Status;
import com.maksim.submissionAcceptorService.entity.Submission;
import com.maksim.submissionAcceptorService.repository.SubmissionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

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

    private KafkaTemplate<Integer, SolutionSubmittedEvent> kafkaTemplate;

    private SubmissionRepository submissionRepository;

    private RestTemplate restTemplate;

    public SubmissionService(KafkaTemplate<Integer, SolutionSubmittedEvent> kafkaTemplate,
                             SubmissionRepository submitSolutionRepository,
                             RestTemplate rest) {
        this.kafkaTemplate = kafkaTemplate;
        this.submissionRepository = submitSolutionRepository;
        this.restTemplate = rest;
    }

    public List<GetSubmissionDto> getAllSubmissions(){
        return submissionRepository.customFindAll();
    }
    public GetSubmissionDto getSubmission(long id){
        Submission submission = submissionRepository.findById(id).orElseThrow(() -> new RuntimeException("No submission with id " + id + " found"));
        return new ObjectMapper().convertValue(submission, GetSubmissionDto.class);
    }

    public long createSubmission(SubmitSolutionTextDto solution, int userId) throws ExecutionException, InterruptedException {
        ProblemDto problem = getProblemConstraints(solution.getProblemId());
        if (problem == null)
            throw new RuntimeException("No problem found with id " + solution.getProblemId());

        Submission submission = new Submission(userId, problem.getId(), LocalDateTime.now(), solution.getSource(), solution.getLanguage(), Status.IN_QUEUE, 0, 0, 0);
        submission = submissionRepository.save(submission);
        sendSubmissionEvent(submission, problem);
        return submission.getId();
    }

    private ProblemDto getProblemConstraints(int problemId) {
        ResponseEntity<ProblemDto> response = restTemplate.getForEntity("http://" +PROBLEM_SERVICE_URL + "/problem/{id}/constraints",
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

        var record = new ProducerRecord<>(TOPIC_NAME, new Random(System.currentTimeMillis()).nextInt(), event);
        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());
        var sendResult = kafkaTemplate.send(record).get();

//        var sendResult = kafkaTemplate.send(TOPIC_NAME, event).get();

        log.error("Message has been sent to topic {} in partitions {}", TOPIC_NAME, sendResult.getRecordMetadata().partition());
    }

    public void saveVerdict(SolutionJudgedEvent ev){
        Submission submission = submissionRepository.findById(ev.getSubmissionId()).get();
        submission.setStatus(ev.getStatus());
        submission.setExecutionTime(ev.getExecutionTime());
        submission.setTestNum(ev.getTestNum());
        submission.setUsedMemory(ev.getMemory());
    }

}
