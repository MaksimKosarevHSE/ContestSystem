package com.maksim.testingService.event;

import com.maksim.testingService.entity.ProcessedEvent;
import com.maksim.testingService.service.JudgingSystemService;
import com.maksim.testingService.respository.ProcessedEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class EventListener {

    private final JudgingSystemService testSystem;
    private final ProcessedEventRepository processedEventRepository;

    EventListener(JudgingSystemService testSystem, ProcessedEventRepository processedEventRepository) {
        this.testSystem = testSystem;
        this.processedEventRepository = processedEventRepository;
    }


    @KafkaListener(topics = "solution-submitted-event-topic", containerFactory = "factory1", concurrency = "2")
    public void handle(@Payload SolutionSubmittedEvent solutionEvent,
                       @Header("event-id") String msgId) throws IOException, InterruptedException, ExecutionException {
        ProcessedEvent ev = processedEventRepository.findByMessageId(msgId);
        if (ev != null) return;
        testSystem.processSubmission(solutionEvent);
        processedEventRepository.save(new ProcessedEvent(msgId));
    }

}
