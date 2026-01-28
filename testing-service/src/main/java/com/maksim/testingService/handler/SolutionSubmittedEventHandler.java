package com.maksim.testingService.handler;

import com.maksim.testingService.event.SolutionSubmittedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component

public class SolutionSubmittedEventHandler {
    @Autowired
    private TestSystem testSystem;

    @KafkaListener(topics="solution-submitted-event-topic", containerFactory = "fact1")
    public void handle(SolutionSubmittedEvent event) throws IOException {
        testSystem.submissionProcessWorker(event,1);
    }
}
