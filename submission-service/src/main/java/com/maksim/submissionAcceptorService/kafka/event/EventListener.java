package com.maksim.submissionAcceptorService.kafka.event;

import com.maksim.submissionAcceptorService.enums.Status;
import com.maksim.submissionAcceptorService.service.JudgingProgressCacheService;
import com.maksim.submissionAcceptorService.service.JudgingProgressNotificationService;
import com.maksim.submissionAcceptorService.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventListener {
    private final SubmissionService service;

    private final JudgingProgressNotificationService judgingProgressNotificationService;

    private final JudgingProgressCacheService judgingProgressCacheService;

    @KafkaListener(topics = "submission-jugding-progress-event-topic", containerFactory = "factory1")
    public void handle(@Payload SubmissionJudgingProgressEvent solutionEvent) {

        if (solutionEvent.getStatus() == Status.TESTING) {
            // cache test num
            judgingProgressCacheService.cacheTestNumAsync(solutionEvent.getSubmissionId(), solutionEvent.getTestNum());
        } else {
            service.processJudgedSolution(solutionEvent);
        }
        // notify about submission status
        judgingProgressNotificationService.notifyProgressAsync(solutionEvent);
    }
}
