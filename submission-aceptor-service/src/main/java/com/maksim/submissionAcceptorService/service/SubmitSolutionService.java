package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.service.DTO.SubmitSolutionDto;
import com.maksim.submissionAcceptorService.service.event.SolutionSubmittedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class SubmitSolutionService {
    @Autowired
    KafkaTemplate<Integer, SolutionSubmittedEvent> kafkaTemplate;
    public int createSubmission(SubmitSolutionDto solution, int userId){
        // типа сохранили в бд
        int id = 1;
        SolutionSubmittedEvent event = new SolutionSubmittedEvent(solution.getProblemId(), userId, id, solution.getSource(), solution.getLanguage());
        var future = kafkaTemplate.send("solution-submitted-event-topic", id, event);
        future.whenComplete((result, ex)->
       {
           if (ex != null){
               System.out.println(ex.getMessage());
           } else {
               System.out.println("SUCCESS");
           }
       });

        return id;
    }

}
