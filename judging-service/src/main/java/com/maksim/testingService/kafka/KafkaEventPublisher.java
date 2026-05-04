package com.maksim.testingService.kafka;

import com.maksim.common.enums.Status;
import com.maksim.common.event.SolutionJudgedEvent;
import com.maksim.testingService.mapper.VerdictMapper;
import com.maksim.testingService.service.model.VerdictInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.producer.ProducerRecord;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    @Value("${test.case.judged.event.topic}")
    private String testCaseJudgedEventTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final VerdictMapper verdictMapper;

    private final Integer KAFKA_TIMEOUT_SEC = 5;

    public void send(String topic, Object payload) {
        try {
            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, payload);
            record.headers().add("event-id", UUID.randomUUID().toString().getBytes());
            kafkaTemplate.send(record).get(KAFKA_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Failed to send event to Kafka topic {}: {}", topic, e.getMessage());
            throw new RuntimeException("Kafka send failed", e);
        }
    }

    public void sendProgressAsync(Long submissionId, Integer testNum) {
        SolutionJudgedEvent event = SolutionJudgedEvent.builder()
                .submissionId(submissionId)
                .testNum(testNum)
                .status(Status.TESTING).build();
        kafkaTemplate.send(testCaseJudgedEventTopic, event);
    }

    public void sendVerdict(Long submissionId, VerdictInfo verdictInfo) {
        SolutionJudgedEvent event = verdictMapper.toEvent(verdictInfo);
        event.setSubmissionId(submissionId);
        send(testCaseJudgedEventTopic, event);
    }
}
