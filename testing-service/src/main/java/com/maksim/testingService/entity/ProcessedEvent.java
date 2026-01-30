package com.maksim.testingService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "processed_events")
public class ProcessedEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false, unique = true)
    private String messageId;

    @Column
    private int userId;


    public ProcessedEvent(String messageId, int userId) {
        this.messageId = messageId;
        this.userId = userId;
    }

}
