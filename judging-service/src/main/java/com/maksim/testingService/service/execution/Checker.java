package com.maksim.testingService.service.execution;

import com.maksim.common.enums.ProgrammingLanguage;
import com.maksim.common.enums.Status;
import com.maksim.testingService.exception.BadVerdictException;
import com.maksim.testingService.service.model.VerdictInfo;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class Checker {
    private static final int JUDGING_TIME_LIMIT_MS = 10_000;

    public void exactMatchCheck(Path jurySolution, Path contestantSolution, VerdictInfo verdictInfo) {
        try (BufferedReader judgeReader = Files.newBufferedReader(jurySolution);
             BufferedReader contestantReader = Files.newBufferedReader(contestantSolution)) {
            int lineCnt = 1;
            String line1;
            String line2;
            while (true) {
                line1 = judgeReader.readLine();
                line2 = contestantReader.readLine();
                if (line1 == null && line2 == null) {
                    break;
                }
                if (line1 == null || line2 == null) {
                    verdictInfo.setStatus(Status.WRONG_ANSWER);
                    String msg = line1 == null
                            ? "The number of lines is less than that of the judge's solution"
                            : "The number of lines is greater that that of the judge's solution";
                    throw new BadVerdictException(msg);
                }

                line1 = line1.replaceAll("(\\n|\\s)", "");
                line2 = line2.replaceAll("(\\n|\\s)", "");

                if (!line1.equals(line2)) {
                    verdictInfo.setStatus(Status.WRONG_ANSWER);
                    throw new BadVerdictException("The line " + lineCnt + " is different from the judge's solution");
                }
                lineCnt++;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void customCheck(Path checker, ProgrammingLanguage checkerLang, Path inputFile, Path contestantSolution, Path checkerOutFile, VerdictInfo verdictInfo) {
        String[] run = checkerLang.getRunCommand(checker);
        List<String> command = new ArrayList<>(List.of(run));
        command.addAll(List.of(inputFile.toString(), contestantSolution.toString(), checkerOutFile.toString()));
        ProcessBuilder builder = new ProcessBuilder(command);
        try {
            Process process = builder.start();
            boolean finished = process.waitFor(JUDGING_TIME_LIMIT_MS, TimeUnit.MILLISECONDS);
            if (!finished) {
                throw new RuntimeException("Checker TL error");
            }
            switch (process.exitValue()) {
                case 0:
                    break;
                case 1:
                    verdictInfo.setStatus(Status.WRONG_ANSWER);
                    throw new BadVerdictException(new String(Files.readAllBytes(checkerOutFile)));
                default:
                    throw new RuntimeException("Checker RE error");
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
