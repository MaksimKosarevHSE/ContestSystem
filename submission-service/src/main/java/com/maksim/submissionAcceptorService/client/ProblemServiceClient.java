package com.maksim.submissionAcceptorService.client;

import com.maksim.submissionAcceptorService.dto.problem.ProblemConstrainsResponseDto;
import com.maksim.submissionAcceptorService.exception.ResourceNotFoundException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
public class ProblemServiceClient {

    @Value("${problem.service.url}")
    private String problemServiceUrl;

    private final RestTemplate restTemplate;


    public ProblemConstrainsResponseDto getProblemConstraints(Integer problemId, Integer contestId) {
        String url;
        if (contestId == null) {
            url = MessageFormat.format("{0}/api/problem/{1}/constraints", problemServiceUrl, problemId);
        } else {
            url = MessageFormat.format("{0}/api/contest/{1}/problem/{2}/constraints", problemServiceUrl, contestId, problemId);
        }

        try {
            ResponseEntity<ProblemConstrainsResponseDto> response = restTemplate.getForEntity(
                    url, ProblemConstrainsResponseDto.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Problem with id " + problemId + " is not found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}