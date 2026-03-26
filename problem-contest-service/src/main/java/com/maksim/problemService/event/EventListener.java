package com.maksim.problemService.event;

import com.maksim.problemService.entity.ProcessedEvent;
import com.maksim.problemService.repository.ProcessedEventRepository;
import com.maksim.problemService.service.StandingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {

    private final StandingsService standingsService;

    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "standings-update-event-topic", containerFactory = "factory1")
    private void standingsUpdateEventHandler(@Payload StandingsUpdateEvent event,
                                             @Header("event-id") UUID eventId) {
        if (processedEventRepository.existsByEventId(eventId)){
            log.info("Duplicate standings update event with id {}", eventId);
            return;
        }
        standingsService.handleUpdateEvent(event);
        processedEventRepository.save(new ProcessedEvent(eventId));
    }

}
