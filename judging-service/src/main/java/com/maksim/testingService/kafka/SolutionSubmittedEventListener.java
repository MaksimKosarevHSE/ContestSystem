package com.maksim.testingService.kafka;

import com.maksim.testingService.entity.ProcessedEvent;
import com.maksim.testingService.event.SolutionSubmittedEvent;
import com.maksim.testingService.service.JudgingManager;
import com.maksim.testingService.respository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolutionSubmittedEventListener {

    private final JudgingManager judgingManager;

    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "solution-submitted-event-topic", containerFactory = "factory1", concurrency = "2")
    public void handle(@Payload SolutionSubmittedEvent solutionEvent,
                       @Header("event-id") String eventId) {
        if (processedEventRepository.existsByMessageId(eventId)) {
            return;
        }
        judgingManager.judge(solutionEvent);
        processedEventRepository.save(new ProcessedEvent(eventId));
    }

}
