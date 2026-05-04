package com.maksim.submissionAcceptorService.service;

import com.maksim.common.dto.PageResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionCreateDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionDetailsResponseDto;
import com.maksim.submissionAcceptorService.dto.submission.SubmissionResponseDto;
import com.maksim.common.enums.Status;
import com.maksim.common.event.SolutionJudgedEvent;

public interface SubmissionService {
    SubmissionResponseDto submitSolution(Integer problemId, Integer contestId, Integer userId, SubmissionCreateDto solution);

    SubmissionResponseDto getSubmission(Long submissionId, Integer contestId);

    SubmissionDetailsResponseDto getSubmissionDetails(Long submissionId, Integer contestId, Integer userId);

    PageResponseDto<SubmissionResponseDto> getSubmissions(Integer contestId, Integer problemId, Integer userId, Status status, Integer page, Integer pageSize);

    void processJudgedSolution(SolutionJudgedEvent ev);
}