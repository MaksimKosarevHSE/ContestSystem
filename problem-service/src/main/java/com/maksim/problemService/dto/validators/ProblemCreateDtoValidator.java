package com.maksim.problemService.dto.validators;

import com.maksim.problemService.dto.ProblemCreateDto;
import com.maksim.problemService.entity.CheckerType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;

@Component
public class ProblemCreateDtoValidator {
    // Я знаю, что вместо этого лучше использовать Hibernate validator
    // Тут я решил так, т.к потребуется кастомная логика проверки файлов (и чтобы понять как Hibernate упрощает жизь)
    public void validateCreateProblemDto(ProblemCreateDto p) {
        String validationMessage = null;

        if (!checkSize(p.getTitle(), 50))
            validationMessage = "Title length must be from 1 to 50 symbol";
        else if (!checkSize(p.getStatement(), 5000))
            validationMessage = "Statement length must be from 1 to 5000 symbols";
        else if (!checkSize(p.getInput(), 5000))
            validationMessage = "Input length must be from 1 to 5000 symbols";
        else if (!checkSize(p.getOutput(), 5000))
            validationMessage = "Output length must be from 1 to 5000 symbols";
        else if (p.getNotes().trim().length() >= 5000)
            validationMessage = "Notes length must be from 1 to 5000 symbols";
        else if (p.getSamplesCount() >= 15)
            validationMessage = "Samples count must be from 0 to 15 symbols";
        else if (p.getSamplesCount() != p.getSampleInput().size())
            validationMessage = "The number of sample inputs does not match with samples count";
        else if (p.getSamplesCount() != p.getSampleOutput().size())
            validationMessage = "The number of sample outputs does not match with samples count";
        else if (!(0 < p.getComplexity() && p.getComplexity() <= 1000) || p.getComplexity() % 100 != 0)
            validationMessage = "Complexity of problem must be from 100 to 1000 and divisible by 100 (100, 200, ..., 1000)";
        else if (!(1 <= p.getCompileTimeLimit() && p.getCompileTimeLimit() <= 10))
            validationMessage = "Compile time limit must be from 1 to to 10 seconds";
        else if (!(0 < p.getTimeLimit() && p.getTimeLimit() <= 5))
            validationMessage = "Time limit must be greater than 0 and less or equal than 5 seconds";
        else if (!(0 < p.getMemoryLimit() && p.getMemoryLimit() <= 512))
            validationMessage = "Memory limit must be greater than 0 and less or equal than 512 mb";
        else if (!(1 <= p.getTestCasesNum() && p.getTestCasesNum() <= 15))
            validationMessage = "Count of test cases must be from 1 to 250";
        else if (p.getInputTestCases().size() != p.getTestCasesNum())
            validationMessage = "Count of input test cases files does not match with number of test cases";
        else if (p.getOutputTestCases().size() != p.getTestCasesNum())
            validationMessage = "Count of output test cases files does not match with number of test cases";
        else if (p.getCheckerType() == CheckerType.CUSTOM_CHECKER && p.getFileSourceChecker() == null)
            validationMessage = "User's checker is not provided";

        if (validationMessage != null) throw new NotValidDtoException(validationMessage);

        var inputFilePrefixes = getPrefixes(p.getInputTestCases(), "input", ".in");
        var outputFilePrefixes = getPrefixes(p.getOutputTestCases(), "output", ".out");

        for (int i = 1; i <= p.getTestCasesNum(); i++){
            if (!inputFilePrefixes.contains(i)){
                throw new NotValidDtoException("No input file found for test case " + i);
            } else if (!outputFilePrefixes.contains(i)){
                throw new NotValidDtoException("No output file found for test case " + i);
            }
        }

    }

    private HashSet<Integer> getPrefixes(List<MultipartFile> files, String fileType, String neededSuffix) {
        var inputFilesPrefix = new HashSet<Integer>();

        for (var inp : files) {
            int idx;
            if (inp.getOriginalFilename() != null && (idx = inp.getOriginalFilename().lastIndexOf(neededSuffix)) != -1) {
                boolean badFileName = false;
                try {
                    int fileName = Integer.parseInt(inp.getOriginalFilename().substring(0, idx));
                    inputFilesPrefix.add(fileName);
                    if (!(1 <= fileName && fileName <= files.size())) badFileName = true;
                } catch (NumberFormatException ex) {
                    badFileName = true;
                }

                if (badFileName){
//                    throw new NotValidDtoException(files.size())
                    throw new NotValidDtoException("Incorrect name of " + fileType + " file \"" + inp.getOriginalFilename() + "\". File must have prefix from 1 to total count of test cases");

                }

            } else {
                throw new NotValidDtoException("Incorrect name of " + fileType + " file \"" + inp.getOriginalFilename() + "\". File must ends with " + neededSuffix);
            }
        }
        return inputFilesPrefix;
    }

    private boolean checkSize(String s, int ub) {
        if (s.isBlank() || s.trim().length() >= ub) return false;
        return true;
    }

}
