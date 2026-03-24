package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.kafka.event.SubmissionJudgingProgressEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JudgingProgressNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    public void notifyProgressAsync(SubmissionJudgingProgressEvent event) {
        try {
            var destination = "/topic/submissions/" + event.getSubmissionId();
            messagingTemplate.convertAndSend(destination, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
