package com.maksim.submissionAcceptorService.repository;

import com.maksim.submissionAcceptorService.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmitSolutionRepository extends JpaRepository<Submission, Long> {
    
}
