package com.maksim.problemService.validators;

import com.maksim.problemService.dto.problem.ProblemCreateDto;
import com.maksim.problemService.enums.CheckerType;
import com.maksim.problemService.exception.BadRequestException;
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
        if (problem.checkerType() == CheckerType.DEFAULT_EXACT_MATCH_CHECKER)
            return;

        var file = problem.fileSourceChecker();

        if (file == null || file.isEmpty())
            throw new BadRequestException("Custom checker is not provided");
    }

    private void validateTestFiles(ProblemCreateDto problem) {
        int num = problem.testCasesNum();
        var inputFiles = problem.inputTestCases();
        var outputFiles = problem.outputTestCases();

        if (inputFiles == null || inputFiles.size() != num)
            throw new BadRequestException("Count of input test files must be " + num);

        if (outputFiles == null || outputFiles.size() != num)
            throw new BadRequestException("Count of output test files must be " + num);

        List<String> inputFileNames;
        List<String> outputFileNames;
        try {
            inputFileNames = problem.inputTestCases().stream()
                    .map(MultipartFile::getOriginalFilename).sorted().toList();
            outputFileNames = problem.outputTestCases().stream()
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
                throw new BadRequestException("Missing file with name " + inFile + ". Input files must be numbered named from 1.in to " + num + ".out");
            if (pos2 < 0)
                throw new BadRequestException("Missing file with name " + outFile + ". Output files must be numbered named from 1.out to " + num + ".out");

            if (inputFiles.get(pos1) == null || outputFiles.get(pos2) == null) {
                throw new RuntimeException("Unexpected error");
            }
        }

    }

    private void validateSamples(ProblemCreateDto dto) {
        int count = dto.samplesCount();
        if (count == 0) return;
        if (dto.sampleInput() == null || dto.sampleInput().size() != count)
            throw new BadRequestException("Inputs of samples must be equal to samples count");
        if (dto.sampleOutput() == null || dto.sampleOutput().size() != count)
            throw new BadRequestException("Outputs of samples must be equal to samples count");
    }

}
