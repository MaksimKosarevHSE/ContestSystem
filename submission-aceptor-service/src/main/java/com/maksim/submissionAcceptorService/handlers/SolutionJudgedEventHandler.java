package com.maksim.submissionAcceptorService.handlers;


import com.maksim.submissionAcceptorService.dto.SolutionJudgedEvent;
import com.maksim.submissionAcceptorService.service.SolutionSubmittedEvent;
import com.maksim.submissionAcceptorService.service.SubmissionService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SolutionJudgedEventHandler {
    private SubmissionService service;

    public SolutionJudgedEventHandler(SubmissionService service) {
        this.service = service;
    }

    @KafkaListener(topics = "solution-judged-event-topic", containerFactory = "factory1")
    @Transactional
    public void handle(@Payload SolutionJudgedEvent solutionEvent) throws IOException, InterruptedException {
        log.error("FUCK");
        service.saveVerdict(solutionEvent);
        log.error("SUCCESS");
    }
}
