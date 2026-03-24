package com.maksim.problemService.service;


import com.maksim.problemService.dto.mapper.ProblemMapper;
import com.maksim.problemService.dto.problem.ProblemCreateDto;
import com.maksim.problemService.dto.problem.ProblemSignatureResponseDto;
import com.maksim.problemService.dto.problem.SendTestCasesToJudgeServiceDto;
import com.maksim.problemService.exception.ResourceNotFoundException;
import com.maksim.problemService.validators.ProblemValidator;
import com.maksim.problemService.dto.problem.ProblemConstraints;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.repository.ProblemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
public class ProblemService {
    private final ProblemRepository problemRepository;

    private final ProblemValidator problemValidator;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ProblemMapper problemMapper;

    @Value("${test.service.url}")
    private String TEST_SERVICE_URL;

    ProblemService(ProblemRepository pr, ProblemValidator problemValidator, ProblemMapper problemMapper) {
        this.problemRepository = pr;
        this.problemValidator = problemValidator;
        this.problemMapper = problemMapper;
    }

    public Problem findById(int id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No problem found with id " + id));
    }

    public ProblemConstraints getConstraints(int id) {
        return problemRepository.getProblemConstraints(id)
                .orElseThrow(() -> new ResourceNotFoundException("No problem found with id " + id));
    }

    public Page<ProblemSignatureResponseDto> getProblemsSignaturesPage(int pageNumber, int pageSize) {
        return problemRepository.getProblemsSignatures(PageRequest.of(pageNumber - 1, pageSize));
    }

    public Problem createProblem(ProblemCreateDto problemCreateDto, int creatorId) {
        problemValidator.validate(problemCreateDto);

        Problem problem = problemMapper.toEntity(problemCreateDto);
        problem.setCreatorId(creatorId);

        var saveTestsDto = SendTestCasesToJudgeServiceDto.from(problemCreateDto);

        problem = problemRepository.save(problem);
        saveTestsDto.setProblemId(problem.getId());

        ResponseEntity<String> response = restTemplate
                .postForEntity(TEST_SERVICE_URL + "/append-tests", saveTestsDto, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException(response.getBody());
        }
        return problem;
    }


}

