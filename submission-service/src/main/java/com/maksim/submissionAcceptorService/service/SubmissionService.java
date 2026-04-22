package com.maksim.submissionAcceptorService.service;

import com.maksim.submissionAcceptorService.dto.PageResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionCreateDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionDetailsResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionResponseDto;
import com.maksim.submissionAcceptorService.enums.Status;
import com.maksim.submissionAcceptorService.event.SolutionJudgedEvent;

public interface SubmissionService {
    SubmissionResponseDto submitSolution(Integer problemId, Integer contestId, Integer userId, SubmissionCreateDto solution);

    SubmissionResponseDto getSubmission(Long submissionId, Integer contestId);

    SubmissionDetailsResponseDto getSubmissionDetails(Long submissionId, Integer contestId, Integer userId);

    PageResponseDto<SubmissionResponseDto> getSubmissions(Integer contestId, Integer problemId, Integer userId, Status status, Integer page);

    void processJudgedSolution(SolutionJudgedEvent ev);
}