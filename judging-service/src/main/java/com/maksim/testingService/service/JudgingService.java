package com.maksim.testingService.service;

import com.maksim.common.dto.problem.SaveTestCasesRequestDto;
import com.maksim.common.enums.CheckerType;
import com.maksim.testingService.exception.JuryCompilationException;
import com.maksim.testingService.service.model.TestCasesMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class JudgingService {
    private static final int JURY_COMPILATION_TIME_LIMIT_MS = 10_000;

    @Value("${judging.tests.dir}")
    private String testDir;

    private final ObjectMapper objectMapper;

    public void saveTests(SaveTestCasesRequestDto dto) {
        Path problemDir = Path.of(testDir).resolve("problem_" + dto.problemId());

        try {
            Files.createDirectories(problemDir);
            for (int i = 0; i < dto.countOfTestCases() * 2; i++) {
                Path filePath = Files.createFile(problemDir.resolve(dto.testFilesNames().get(i)));
                Files.write(filePath, dto.testFilesContent().get(i));
            }

            TestCasesMetadata metaData = TestCasesMetadata.from(dto);

            if (dto.checkerType() == CheckerType.CUSTOM_CHECKER) {
                Path checkerFile = Files.createFile(problemDir.resolve("checker" + dto.checkerLanguage().sourceSuffix));
                metaData.setCheckerFileName("checker" + dto.checkerLanguage().compiledSuffix);
                Files.write(checkerFile, dto.checkerSourceCode());

                if (dto.checkerLanguage().needCompilation) {
                    ProcessBuilder builder = new ProcessBuilder(dto.checkerLanguage().getCompileCommand(checkerFile));
                    Process process = builder.start();
                    boolean success = process.waitFor(JURY_COMPILATION_TIME_LIMIT_MS, TimeUnit.MILLISECONDS);
                    if (!success) {
                        throw new JuryCompilationException("Compilation stage of checker exceeded 10s limit");
                    }
                    if (process.exitValue() != 0) {
                        throw new JuryCompilationException(process.getOutputStream().toString());
                    }
                    Files.delete(checkerFile);
                }
            }

            String metaDataJson = objectMapper.writeValueAsString(metaData);
            Path metaFile = Files.createFile(problemDir.resolve("meta.json"));
            Files.writeString(metaFile, metaDataJson);
        } catch (IOException | InterruptedException | JuryCompilationException ex) {
            log.error("Exception occur while saving test files {}", ex.getMessage());
            try {
                Files.deleteIfExists(problemDir);
            } catch (IOException ignored) {
            }
            throw new RuntimeException(ex);
        }
    }
}
