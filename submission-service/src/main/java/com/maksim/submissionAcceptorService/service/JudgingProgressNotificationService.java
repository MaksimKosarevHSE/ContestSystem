package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.event.SolutionJudgedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
@RequiredArgsConstructor
public class JudgingProgressNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    public void notifyProgressAsync(SolutionJudgedEvent event) {
        try {
            String destination = "/topic/submissions/" + event.getSubmissionId();
            messagingTemplate.convertAndSend(destination, event);
        } catch (Exception e) {
            log.error("Error sending the message: {}", e.getMessage());
        }
    }
}
