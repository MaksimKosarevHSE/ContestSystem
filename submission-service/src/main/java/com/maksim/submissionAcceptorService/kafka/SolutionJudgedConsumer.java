package com.maksim.submissionAcceptorService.kafka;

import com.maksim.submissionAcceptorService.enums.Status;
import com.maksim.submissionAcceptorService.event.SolutionJudgedEvent;
import com.maksim.submissionAcceptorService.service.JudgingProgressCacheService;
import com.maksim.submissionAcceptorService.service.JudgingProgressNotificationService;
import com.maksim.submissionAcceptorService.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SolutionJudgedConsumer {

    private final SubmissionService submissionService;

    private final JudgingProgressCacheService judgingProgressCacheService;

    private final JudgingProgressNotificationService judgingProgressNotificationService;

    @KafkaListener(topics = "submission-jugding-progress-event-topic", containerFactory = "factory1")
    public void handle(@Payload SolutionJudgedEvent event) {
        if (event.getStatus() == Status.TESTING) {
            judgingProgressCacheService.cacheTestNumAsync(event.getSubmissionId(), event.getTestNum());
        } else {
            submissionService.processJudgedSolution(event);
        }
        judgingProgressNotificationService.notifyProgressAsync(event);
    }
}