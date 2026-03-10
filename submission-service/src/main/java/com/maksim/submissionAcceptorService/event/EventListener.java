package com.maksim.submissionAcceptorService.event;

import com.maksim.submissionAcceptorService.service.SubmissionService;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EventListener {
    private final SubmissionService service;

    public EventListener(SubmissionService service) {
        this.service = service;
    }

    @KafkaListener(topics = "solution-judged-event-topic", containerFactory = "factory1")
    @Transactional
    public void handle(@Payload SolutionJudgedEvent solutionEvent){
        service.saveVerdict(solutionEvent);
    }
}
