package com.maksim.submissionAcceptorService.service.outbox;

import com.maksim.submissionAcceptorService.entity.OutboxEvent;
import com.maksim.submissionAcceptorService.kafka.KafkaEventPublisher;
import com.maksim.submissionAcceptorService.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public void publishEvents() {
        outboxEventRepository.findAll()
                .forEach(this::process);
    }

    private void process(OutboxEvent outboxEvent) {
        try {
            kafkaEventPublisher.processOutboxEvent(outboxEvent);
            outboxEventRepository.delete(outboxEvent);
        } catch (Exception e) {
            log.error("Failed to process outbox event {}", outboxEvent.getEventId(), e);
        }
    }

    public void save(String topic, Object payload) {
        try {
            OutboxEvent event = OutboxEvent.builder()
                    .eventId(UUID.randomUUID())
                    .eventType(topic)
                    .payload(objectMapper.writeValueAsString(payload))
                    .build();
            outboxEventRepository.save(event);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
    }
}
