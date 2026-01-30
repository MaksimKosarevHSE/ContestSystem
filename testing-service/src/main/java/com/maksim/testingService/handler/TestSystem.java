package com.maksim.testingService.handler;

import com.maksim.testingService.DTO.VerdictInfo;
import com.maksim.testingService.enums.Status;
import com.maksim.testingService.event.SolutionSubmittedEvent;
import com.maksim.testingService.exceptions.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class TestSystem {
    private final String PATH_TO_TESTS = "judge/tests";
    private final String PATH_TO_SESSION_STORE = "judge/sessions";
    private final String OUTPUT_FILE_NAME = "output.txt";
    private final String SOURCE_FILE_NAME = "main";
    private Random rand = new Random();


    public void processSubmission(SolutionSubmittedEvent submissionMeta, int workerId) throws IOException, InterruptedException {
            try {
                submissionMeta.setTimeLimit(5);
                log.debug("Worker {} started to test submission {}", workerId, submissionMeta.getSubmissionId());
                log.info("{}",submissionMeta);
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
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.error("Worker {} thread is interrupted. {}", workerId, ex.getMessage());
                throw ex;
            } catch (IOException ex) {
                log.error("Server error while testing {}. {}", submissionMeta.getSubmissionId(), ex.getMessage());
                throw ex;

            } catch (BadVerdict ex) {
                log.debug("BadVerdict of submission {}. {}", submissionMeta.getSubmissionId(), ex.getMessage());
            }
    }


    private void testSolution(Path compiledFile, Path sessionDir, SolutionSubmittedEvent submissionMeta, VerdictInfo verdictInfo) throws IOException, InterruptedException {
        Path judgeTestDir = Path.of(PATH_TO_TESTS).resolve("problem_" + submissionMeta.getProblemId());
        int testsCnt = Integer.parseInt(Files.readString(judgeTestDir.resolve("meta.txt")));

        for (int i = 1; i <= testsCnt; i++) {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(submissionMeta.getLanguage().getRunCommand(compiledFile));
            pb.redirectInput(new File(judgeTestDir.resolve(i + ".in").toString()));

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
            exactMatchCheck(judgeTestDir.resolve(i + ".out"), process.getInputStream(), verdictInfo, i);
        }

        verdictInfo.setStatus(Status.OK);
    }


    private Path compileSolution(Path sessionDir, Path sourcePath, SolutionSubmittedEvent submission, VerdictInfo verdictInfo) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        Path outputPath = sessionDir.resolve(OUTPUT_FILE_NAME);
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


    public static void exactMatchCheck(Path judgeSolution, InputStream contestantSolution, VerdictInfo verdictInfo, int testNum) throws IOException {
        try (BufferedReader judgeReader = new BufferedReader(new FileReader(judgeSolution.toFile()));
             BufferedReader conReader = new BufferedReader(new InputStreamReader(contestantSolution))) {

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
                    throw new BadVerdict("The line " +  lineCnt + " is different from the judge's solution");
                }
                lineCnt++;
            }
        }
    }
}




