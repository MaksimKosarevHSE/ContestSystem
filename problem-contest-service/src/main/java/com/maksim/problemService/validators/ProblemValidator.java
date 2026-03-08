package com.maksim.problemService.validators;

import com.maksim.problemService.dto.problem.ProblemCreateDto;
import com.maksim.problemService.entity.CheckerType;
import com.maksim.problemService.exception.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Component
public class ProblemValidator {

    // этот класс делает доп проверки, остальное в аннатациях

    public void validate(ProblemCreateDto problem) {
        validateSamples(problem);
        validateChecker(problem);
        validateTestFiles(problem);
    }


    private void validateChecker(ProblemCreateDto problem) {
        if (problem.getCheckerType() == CheckerType.DEFAULT_EXACT_MATCH_CHECKER)
            return;

        var file = problem.getFileSourceChecker();

        if (file == null || file.isEmpty())
            throw new ValidationException("Custom checker is not provided");
    }

    private void validateTestFiles(ProblemCreateDto problem) {
        int num = problem.getTestCasesNum();
        var inputFiles = problem.getInputTestCases();
        var outputFiles = problem.getOutputTestCases();

        if (inputFiles == null || inputFiles.size() != num)
            throw new ValidationException("Count of input test files must be " + num);

        if (outputFiles == null || outputFiles.size() != num)
            throw new ValidationException("Count of output test files must be " + num);

        List<String> inputFileNames;
        List<String> outputFileNames;
        try {
            inputFileNames = problem.getInputTestCases().stream()
                    .map(MultipartFile::getOriginalFilename).sorted().toList();
            outputFileNames = problem.getInputTestCases().stream()
                    .map(MultipartFile::getOriginalFilename).sorted().toList();
        } catch (Exception ex) {
            throw ex;
        }

        for (int i = 1; i <= num; i++) {
            String inFile = i + ".in";
            String outFile = i + ".out";
            int pos1 = Collections.binarySearch(inputFileNames, inFile);
            int pos2 = Collections.binarySearch(outputFileNames, outFile);

            if (pos1 < 0)
                throw new ValidationException("Missing file with name " + inFile + ". Input files must be numbered named from 1.in to " + num + ".out");
            if (pos2 < 0)
                throw new ValidationException("Missing file with name " + inFile + ". Output files must be numbered named from 1.out to " + num + ".out");

            if (inputFiles.get(pos1) == null || outputFiles.get(pos2) == null) {
                throw new RuntimeException("Unexpected error");
            }
        }

    }

    private void validateSamples(ProblemCreateDto dto) {
        int count = dto.getSamplesCount();
        if (count == 0) return;
        if (dto.getSampleInput() == null || dto.getSampleInput().size() != count)
            throw new ValidationException("Inputs of samples must be equal to samples count");
        if (dto.getSampleOutput() == null || dto.getSampleOutput().size() != count)
            throw new ValidationException("Outputs of samples must be equal to samples count");
    }

}
