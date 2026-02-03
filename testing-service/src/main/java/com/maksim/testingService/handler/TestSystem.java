package com.maksim.testingService.handler;

import com.maksim.testingService.DTO.VerdictInfo;
import com.maksim.testingService.entity.CheckerType;
import com.maksim.testingService.entity.TestsMetadata;
import com.maksim.testingService.enums.ProgrammingLanguage;
import com.maksim.testingService.enums.Status;
import com.maksim.testingService.event.SolutionSubmittedEvent;
import com.maksim.testingService.exceptions.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


@Service
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class TestSystem {
    private final String PATH_TO_TESTS = "judge/tests";
    private final String PATH_TO_SESSION_STORE = "judge/sessions";
    private final String SOURCE_FILE_NAME = "main";
    private final int JUDGING_TIME_LIMIT = 10;
    private final String CONTESTANT_OUT_FILE_NAME = "output.out";
    private final String CHECKER_OUT_FILE_NAME = "checker_.out";
    private Random rand = new Random();


    public void processSubmission(SolutionSubmittedEvent submissionMeta, int workerId) throws IOException, InterruptedException {
            try {
                log.debug("Worker {} started to test submission {}", workerId, submissionMeta.getSubmissionId());
                var verdictInfo = new VerdictInfo();
                Path sessionDir = Files.createTempDirectory(Path.of(PATH_TO_SESSION_STORE), null);
                log.info("PATH: {}", sessionDir.toAbsolutePath().toString());
                Path sourceFile = Files.createFile(sessionDir.resolve(SOURCE_FILE_NAME + submissionMeta.getLanguage().sourceSuffix));
                Files.writeString(sourceFile, submissionMeta.getSource());
                Path compiledFile = sourceFile;
                if (submissionMeta.getLanguage().needCompilation) {
                    log.debug("Start compilation stage of submission {}", submissionMeta.getSubmissionId());
                    compiledFile = compileSolution(sessionDir, sourceFile, submissionMeta, verdictInfo);
                    log.debug("Compilation stage of submission {} passed success", submissionMeta.getSubmissionId());
                }
                log.debug("Start testing stage of submission {} ", submissionMeta.getSubmissionId());
                testSolution(compiledFile, sessionDir, submissionMeta, verdictInfo);
                log.debug("Submission {} was tested successfully with verdict {}", submissionMeta.getSubmissionId(), verdictInfo);

                // пишем в бд результат, отправляем в нотификэйшн сервис
            } catch (InterruptedException | IOException ex) {
                Thread.currentThread().interrupt();
                log.error("Server error while testing {}. {}", submissionMeta.getSubmissionId(), ex.getMessage());
                throw ex;
            } catch (BadVerdict ex) {
                log.debug("BadVerdict of submission {}. {}", submissionMeta.getSubmissionId(), ex.getMessage());
            }
    }


    private void testSolution(Path compiledFile,Path sessionDir, SolutionSubmittedEvent submissionMeta, VerdictInfo verdictInfo) throws IOException, InterruptedException {
        Path judgeTestDir = Path.of(PATH_TO_TESTS).resolve("problem_" + submissionMeta.getProblemId());
        TestsMetadata meta = new ObjectMapper().readValue(judgeTestDir.resolve("meta.json"), TestsMetadata.class);
        int testsCnt = meta.getTestCount();
        Path contestantOutFile = sessionDir.resolve(CONTESTANT_OUT_FILE_NAME);
        Path checkerOutFile = sessionDir.resolve(CHECKER_OUT_FILE_NAME);

        for (int i = 1; i <= testsCnt; i++) {
            Files.createFile(contestantOutFile);
            Files.createFile(checkerOutFile);

            ProcessBuilder pb = new ProcessBuilder();
            pb.command(submissionMeta.getLanguage().getRunCommand(compiledFile));
            pb.redirectInput(judgeTestDir.resolve(i + ".in").toFile());
            pb.redirectOutput(contestantOutFile.toFile());

            Process process = pb.start();
            long start = System.currentTimeMillis();
            boolean successEnd = process.waitFor(submissionMeta.getTimeLimit(), TimeUnit.SECONDS);
            int duration = (int) (System.currentTimeMillis() - start);
            verdictInfo.setUsedMemory(123);
            verdictInfo.setExecutionTime(duration);

            if (!successEnd) {
                process.destroyForcibly();
                verdictInfo.setNumOfFailureTest(i);
                verdictInfo.setStatus(Status.TIME_LIMIT);
                throw new BadVerdict("Time limit exceeded");
            }
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                verdictInfo.setNumOfFailureTest(i);
                verdictInfo.setStatus(Status.RUNTIME_ERROR);
                throw new BadVerdict("Runtime error");
            }

            Path answerFile = judgeTestDir.resolve(i + ".out");
            if (meta.getCheckerType() == CheckerType.DEFAULT_EXACT_MATCH_CHECKER){
                exactMatchCheck(answerFile, contestantOutFile.toFile() , verdictInfo, i);
            } else {
                customChecker(judgeTestDir.resolve(meta.getCheckerFileName()),
                        meta.getCheckerLanguage(),
                        CONTESTANT_OUT_FILE_NAME,
                        CHECKER_OUT_FILE_NAME,
                        answerFile.toAbsolutePath().toString(),
                        verdictInfo, i);
            }

        }

        verdictInfo.setStatus(Status.OK);
    }
    private void customChecker(Path checker, ProgrammingLanguage checkerLang,
                               String contestantOutFile, String checkerOutFile, String answerFile,
                               VerdictInfo verdictInfo, int testNum) throws IOException, InterruptedException {
        String[] run = checkerLang.getRunCommand(checker);
        var command = new ArrayList<>(List.of(run));
        command.addAll(List.of(contestantOutFile, checkerOutFile, answerFile));

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        boolean successEnd = process.waitFor(JUDGING_TIME_LIMIT, TimeUnit.SECONDS);
        if (!successEnd){
            log.debug("Checker checks submission too much time");
            throw new RuntimeException("Checker TL error");
        }
        switch (process.exitValue()){
            case 0:
                break;
            case 1:
                verdictInfo.setStatus(Status.WRONG_ANSWER);
                verdictInfo.setNumOfFailureTest(testNum);
                throw new BadVerdict(new String(Files.readAllBytes(Path.of(checkerOutFile))));
            default:
                log.debug("Checker RE error");
                throw new RuntimeException("Checker RE error");
        }
    }

    private Path compileSolution(Path sessionDir, Path sourcePath, SolutionSubmittedEvent submission, VerdictInfo verdictInfo) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        Path outputPath = sessionDir.resolve("compilator.out");
        pb.redirectOutput(outputPath.toFile());
        pb.redirectErrorStream(true);
        String[] compileCommand = submission.getLanguage().getCompileCommand(sourcePath);
        pb.command(compileCommand);

        Process process = pb.start();
        boolean successEnd = process.waitFor(submission.getCompilationTimeLimit(), TimeUnit.SECONDS);
        if (!successEnd) {
            process.destroyForcibly();
            verdictInfo.setStatus(Status.COMPILE_TIME_LIMIT);
            throw new BadVerdict("Compilation time limit");
        }
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            String out = new String(Files.readAllBytes(outputPath));
            verdictInfo.setStatus(Status.COMPILE_ERROR);
            throw new BadVerdict(out);
        }

        String sourcePathString = sourcePath.toString();
        return Path.of(sourcePathString.substring(0, sourcePathString.lastIndexOf(".")) + submission.getLanguage().compiledSuffix);
    }


    public void exactMatchCheck(Path judgeSolution, File contestantSolution, VerdictInfo verdictInfo, int testNum) throws IOException {
        try (BufferedReader judgeReader = new BufferedReader(new FileReader(judgeSolution.toFile()));
             BufferedReader conReader = new BufferedReader(new FileReader(contestantSolution))) {

            int lineCnt = 1;
            String line1, line2;
            while (true) {
                line1 = judgeReader.readLine();
                line2 = conReader.readLine();
                if (line1 == null && line2 == null) {
                    break;
                }
                if (line1 == null || line2 == null) {
                    verdictInfo.setNumOfFailureTest(testNum);
                    verdictInfo.setStatus(Status.WRONG_ANSWER);
                    String msg;
                    if (line1 == null)
                        msg = "The number of lines is less than that of the judge's solution";
                    else
                        msg = "The number of lines is greater that that of the judge's solution";
                    throw new BadVerdict(msg);
                }

                line1 = line1.replaceAll("(\\n|\\s)", "");
                line2 = line2.replaceAll("(\\n|\\s)", "");

                if (!line1.equals(line2)) {
                    verdictInfo.setNumOfFailureTest(testNum);
                    verdictInfo.setStatus(Status.WRONG_ANSWER);
                    throw new BadVerdict("The line " +  lineCnt + " is different from the judge's solution");
                }
                lineCnt++;
            }
        }
    }


}




