package com.maksim.testingService.service.execution;

import com.maksim.common.enums.Status;
import com.maksim.common.event.SolutionSubmittedEvent;
import com.maksim.testingService.exception.BadVerdictException;
import com.maksim.testingService.service.model.VerdictInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Runner {

    public void run(SolutionSubmittedEvent submissionMeta, Path executable, Path inputFile, Path outputFile, VerdictInfo verdictInfo) {
        String[] command = submissionMeta.getLanguage().getRunCommand(executable);
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectInput(inputFile.toFile());
        pb.redirectOutput(outputFile.toFile());
        try {
            Process process = pb.start();
            long start = System.currentTimeMillis();
            boolean finished = process.waitFor(submissionMeta.getTimeLimit(), TimeUnit.MILLISECONDS);
            int duration = (int) (System.currentTimeMillis() - start);

            verdictInfo.setMemory(-1);
            verdictInfo.setExecutionTime(duration);

            if (!finished) {
                process.destroyForcibly();
                verdictInfo.setStatus(Status.TIME_LIMIT);
                throw new BadVerdictException("Time limit exceeded");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                verdictInfo.setStatus(Status.RUNTIME_ERROR);
                throw new BadVerdictException("Runtime error");
            }

        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
