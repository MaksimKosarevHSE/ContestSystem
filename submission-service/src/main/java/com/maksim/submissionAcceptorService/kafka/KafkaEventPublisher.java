package com.maksim.submissionAcceptorService.kafka;

import com.maksim.submissionAcceptorService.entity.OutboxEvent;
import com.maksim.submissionAcceptorService.event.SolutionSubmittedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper om;

    @Value("${outbox.kafka.timeout:5}")
    private int kafkaTimeout;

    public void processOutboxEvent(OutboxEvent outboxEvent) {
        SolutionSubmittedEvent event = om.readValue(outboxEvent.getPayload(), SolutionSubmittedEvent.class);
        try {
            ProducerRecord<String, Object> record =
                    new ProducerRecord<>(outboxEvent.getEventType(), event);
            record.headers().add("event-id", UUID.randomUUID().toString().getBytes());
            kafkaTemplate.send(record).get(kafkaTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Failed to send event to Kafka topic {}: {}", outboxEvent.getEventType(), e.getMessage());
            throw new RuntimeException("Kafka sending failed", e);
        }
    }
}