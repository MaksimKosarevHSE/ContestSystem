package com.maksim.testingService.handler;

import com.maksim.testingService.entity.ProcessedEvent;
import com.maksim.testingService.event.SolutionSubmittedEvent;
import com.maksim.testingService.respository.ProcessedEventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SolutionSubmittedEventHandler {

    private TestSystem testSystem;
    private ProcessedEventRepository processedEventRepository;

    SolutionSubmittedEventHandler(TestSystem testSystem, ProcessedEventRepository processedEventRepository){
        this.testSystem = testSystem;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @KafkaListener(topics="solution-submitted-event-topic", containerFactory = "factory1")
    public void handle(@Payload SolutionSubmittedEvent solutionEvent,
                       @Header("messageId") String msgId,
                       @Header(KafkaHeaders.RECEIVED_KEY) Integer key) throws IOException, InterruptedException {

        ProcessedEvent ev = processedEventRepository.findByMessageId(msgId);
        System.out.println("KEY: " + key);
        if (ev != null){
            System.out.println("DROP");
            return;
        }
        testSystem.processSubmission(solutionEvent,1);

        processedEventRepository.save(new ProcessedEvent(msgId, solutionEvent.getUserId()));
    }

}
