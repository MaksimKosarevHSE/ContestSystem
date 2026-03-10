package com.maksim.problemService.event;

import com.maksim.problemService.service.StandingsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class EventListener {
    private final StandingsService standingsService;

    public EventListener(StandingsService sc) {
        this.standingsService = sc;
    }

    @KafkaListener(topics = "standings-update-event-topic", containerFactory = "factory1")
    private void standingsUpdateEventHandler(@Payload ContestSubmissionWasTestedEvent event) {
        standingsService.handleUpdateEvent(event);
    }

}
