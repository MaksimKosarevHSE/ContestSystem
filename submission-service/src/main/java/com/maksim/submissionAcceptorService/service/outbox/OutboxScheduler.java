package com.maksim.submissionAcceptorService.service.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxEventService outboxEventService;

    @Scheduled(fixedDelay = 10000)
    public void run(){
        log.info("Outbox processing start");
        outboxEventService.publishEvents();
        log.info("Outbox processing end");
    }
}
