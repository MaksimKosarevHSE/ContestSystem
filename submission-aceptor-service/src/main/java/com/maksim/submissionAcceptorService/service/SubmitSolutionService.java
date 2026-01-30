package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.ProgrammingLanguage;
import com.maksim.submissionAcceptorService.SubmitSolutionDto;
import com.maksim.submissionAcceptorService.service.event.SolutionSubmittedEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class SubmitSolutionService {
    Random rand = new Random();
    @Autowired
    KafkaTemplate<Integer, SolutionSubmittedEvent> kafkaTemplate;
    public int createSubmission(SubmitSolutionDto solution, int userId){
        // типа сохранили в бд
        int id = rand.nextInt();
        SolutionSubmittedEvent event = new SolutionSubmittedEvent(1, userId, id,  solution.getSource(), ProgrammingLanguage.Cpp, 1, 500, 5);

        var record = new ProducerRecord<>("solution-submitted-event-topic",
                userId, event);

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes() );
        var future = kafkaTemplate.send(record);

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
