package com.maksim.submissionAcceptorService.service.outbox;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maksim.submissionAcceptorService.entity.OutboxEvent;
import com.maksim.submissionAcceptorService.kafka.KafkaEventPublisher;
import com.maksim.submissionAcceptorService.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaEventPublisher kafkaEventPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void publishEvents() {
        outboxEventRepository.findAll()
                .forEach(this::process);

    }

    private void process(OutboxEvent outboxEvent) {
        try {
            kafkaEventPublisher.processOutboxEvent(outboxEvent);
            outboxEventRepository.delete(outboxEvent);
        } catch (Exception e){
            log.warn("Failed to process event {}", outboxEvent.getEventId());
        }
    }

    public void save(String topic, Object payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            OutboxEvent event = OutboxEvent.builder()
                    .eventType(topic)
                    .payload(payloadJson)
                    .build();
            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}