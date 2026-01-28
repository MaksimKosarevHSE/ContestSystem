package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.service.dto.SubmitSolutionDto;
import com.maksim.submissionAcceptorService.service.event.SolutionSubmittedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SubmitSolutionService {
    Random rand = new Random();
    @Autowired
    KafkaTemplate<Integer, SolutionSubmittedEvent> kafkaTemplate;
    public int createSubmission(SubmitSolutionDto solution, int userId){
        // типа сохранили в бд
        int id = rand.nextInt();
        SolutionSubmittedEvent event = new SolutionSubmittedEvent(solution.getProblemId(), userId, id, solution.getSource(), solution.getLanguage(), 1, 500, 3);
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
