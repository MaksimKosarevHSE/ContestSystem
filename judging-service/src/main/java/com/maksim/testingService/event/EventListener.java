package com.maksim.testingService.event;

import com.maksim.testingService.entity.ProcessedEvent;
import com.maksim.testingService.service.JudgingSystemService;
import com.maksim.testingService.respository.ProcessedEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class EventListener {

    private JudgingSystemService testSystem;
    private ProcessedEventRepository processedEventRepository;

    EventListener(JudgingSystemService testSystem, ProcessedEventRepository processedEventRepository){
        this.testSystem = testSystem;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @KafkaListener(topics="solution-submitted-event-topic", containerFactory = "factory1", concurrency = "2")
    public void handle(@Payload SolutionSubmittedEvent solutionEvent,
                       @Header("messageId") String msgId,
                       @Header(KafkaHeaders.RECEIVED_KEY) Integer key) throws IOException, InterruptedException, ExecutionException {

        ProcessedEvent ev = processedEventRepository.findByMessageId(msgId);
        if (ev != null) return;
        testSystem.processSubmission(solutionEvent,1);
        processedEventRepository.save(new ProcessedEvent(msgId, solutionEvent.getUserId()));
    }

}
