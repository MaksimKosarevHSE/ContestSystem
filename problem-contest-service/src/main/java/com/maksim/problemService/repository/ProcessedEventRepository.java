package com.maksim.problemService.repository;

import com.maksim.problemService.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Integer> {
    boolean existsByEventId (UUID eventId);
}
