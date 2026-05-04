package com.maksim.testingService.service.execution;

import com.maksim.common.enums.Status;
import com.maksim.common.event.SolutionSubmittedEvent;
import com.maksim.testingService.exception.BadVerdictException;
import com.maksim.testingService.service.model.VerdictInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Compiler {

    public Path compile(Path sourceFile, Path outputDir, SolutionSubmittedEvent submission, VerdictInfo verdictInfo) {
        if (!submission.getLanguage().needCompilation) {
            return sourceFile;
        }
        Path errorPath = outputDir.resolve("compile_error.log");
        String[] compileCommand = submission.getLanguage().getCompileCommand(sourceFile);

        ProcessBuilder pb = new ProcessBuilder(compileCommand);
        pb.redirectErrorStream(true);
        pb.redirectOutput(errorPath.toFile());

        try {
            Process process = pb.start();
            boolean finished = process.waitFor(submission.getCompilationTimeLimit(), TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                verdictInfo.setStatus(Status.COMPILE_ERROR);
                throw new BadVerdictException("Compilation time limit");
            }
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                String out = new String(Files.readAllBytes(errorPath));
                verdictInfo.setStatus(Status.COMPILE_ERROR);
                throw new BadVerdictException("Compilation failed:\n" + out);
            }
            String sourcePathString = sourceFile.toString();
            return Path.of(sourcePathString.substring(0, sourcePathString.lastIndexOf(".")) + submission.getLanguage().compiledSuffix);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
