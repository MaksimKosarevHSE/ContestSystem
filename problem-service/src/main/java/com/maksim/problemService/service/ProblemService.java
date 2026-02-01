package com.maksim.problemService.service;


import com.maksim.problemService.dto.ProblemCreateDto;
import com.maksim.problemService.dto.ProblemSignature;
import com.maksim.problemService.dto.SendTestCasesToJudgeServiceDto;
import com.maksim.problemService.dto.validators.NotValidDtoException;
import com.maksim.problemService.dto.validators.ProblemCreateDtoValidator;
import com.maksim.problemService.entity.CheckerType;
import com.maksim.problemService.entity.ProblemConstraints;
import com.maksim.problemService.entity.Problem;
import com.maksim.problemService.repository.ProblemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final ProblemCreateDtoValidator problemCreateDtoValidator;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${test.service.url}")
    private String TEST_SERVICE_URL;

    ProblemService(ProblemRepository pr, ProblemCreateDtoValidator problemCreateDtoValidator) {
        this.problemRepository = pr;
        this.problemCreateDtoValidator = problemCreateDtoValidator;
    }

    public Optional<Problem> findById(int id) {
        return problemRepository.findById(id);
    }

    public ProblemConstraints getConstraints(int id) {
        return problemRepository.getProblemConstraints(id);
    }

    public Page<Problem> getProblemsPage(int pageNumber, int pageSize) {
        return problemRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    public Page<ProblemSignature> getProblemsSignaturesPage(int pageNumber, int pageSize) {
        return problemRepository.getProblemsSignatures(PageRequest.of(pageNumber, pageSize));
    }

    public Problem createProblem(ProblemCreateDto problemCreateDto, int creatorId) throws IOException {
        problemCreateDtoValidator.validateCreateProblemDto(problemCreateDto);

        ObjectMapper mapper = new ObjectMapper();
        Problem problem = mapper.convertValue(problemCreateDto,Problem.class);
        problem.setCreatorId(creatorId);

        var saveTestsDto = new SendTestCasesToJudgeServiceDto();
        saveTestsDto.setCountOfTestCases(problemCreateDto.getTestCasesNum());
        saveTestsDto.setCheckerType(problemCreateDto.getCheckerType());

        if (saveTestsDto.getCheckerType() == CheckerType.CUSTOM_CHECKER){
            saveTestsDto.setCheckerSourceCode(problemCreateDto.getFileSourceChecker().getBytes());
        }
        for (var el : problemCreateDto.getInputTestCases()) {
            saveTestsDto.getTestFilesContent().add(el.getBytes());
            saveTestsDto.getTestFilesNames().add(el.getOriginalFilename());
        }
        for (var el : problemCreateDto.getOutputTestCases()) {
            saveTestsDto.getTestFilesContent().add(el.getBytes());
            saveTestsDto.getTestFilesNames().add(el.getOriginalFilename());
        }

        problem = problemRepository.save(problem);
        ResponseEntity<Object> response = restTemplate.postForEntity("localhost:123", saveTestsDto, Object.class);
        if (response.getStatusCode() != HttpStatus.OK){
            throw new RuntimeException("Can't save tests in test service");
        }
        return problem;
    }


}

