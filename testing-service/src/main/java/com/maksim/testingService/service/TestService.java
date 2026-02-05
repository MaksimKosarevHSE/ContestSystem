package com.maksim.testingService.service;


import com.maksim.testingService.DTO.SaveTestsDto;
import com.maksim.testingService.entity.CheckerType;
import com.maksim.testingService.entity.TestsMetadata;
import com.maksim.testingService.exceptions.JuryCompilationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TestService {
    private final String TEST_DIR_PATH = "/judge/tests";
    private final int JURY_COMPILATION_TIME_LIMIT = 10;

    public void saveTests(SaveTestsDto dto) throws IOException, InterruptedException, JuryCompilationException {
        Path problemDir = Path.of(TEST_DIR_PATH).resolve(Path.of("problem_" + dto.getProblemId()));
        try {
            System.out.println(Files.createDirectories(problemDir).toAbsolutePath());
            System.out.println(dto.getTestFilesContent());
            for (int i = 0; i < dto.getCountOfTestCases() * 2; i++) {
                Path filePath = Files.createFile(problemDir.resolve(dto.getTestFilesNames().get(i)));
                Files.write(filePath, dto.getTestFilesContent().get(i));
            }
            var metaData = new TestsMetadata(dto.getProblemId(), dto.getCountOfTestCases(), dto.getCheckerType(), dto.getCheckerLanguage(), null );

            if (dto.getCheckerType() == CheckerType.CUSTOM_CHECKER) {
                Path checkerFile = Files.createFile(problemDir.resolve("checker" + dto.getCheckerLanguage().sourceSuffix));
                metaData.setCheckerFileName("checker" + dto.getCheckerLanguage().compiledSuffix);
                Files.write(checkerFile, dto.getCheckerSourceCode());
                if (dto.getCheckerLanguage().needCompilation) {
                    ProcessBuilder builder = new ProcessBuilder();
                    builder.command(dto.getCheckerLanguage().getCompileCommand(checkerFile));
                    Process process = builder.start();
                    boolean success = process.waitFor(JURY_COMPILATION_TIME_LIMIT, TimeUnit.SECONDS);
                    if (!success)
                        throw new JuryCompilationException("Compilation stage of checker exceeded 10s limit");
                    if (process.exitValue() != 0)
                        throw new JuryCompilationException(process.getOutputStream().toString());
                    Files.delete(checkerFile);
                }
            }

            String metaDataJson = new ObjectMapper().writeValueAsString(metaData);

            Path metaFile = Files.createFile(problemDir.resolve("meta.json"));
            Files.writeString(metaFile, metaDataJson);

        } catch (IOException | InterruptedException | JuryCompilationException ex) {
            ex.printStackTrace();
            log.error("Exception occur while saving test files {}", ex.getMessage());
            try {
                Files.deleteIfExists(problemDir);
            } catch (IOException e) {
                log.error("Can not delete useless files");
            }
            throw ex;
        }
    }
}
