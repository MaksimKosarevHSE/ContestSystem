package com.maksim.problemService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "processed_events")
public class ProcessedEvent {
    @Id
    private UUID eventId;

    public ProcessedEvent(UUID eventId) {
        this.eventId = eventId;
    }

}
