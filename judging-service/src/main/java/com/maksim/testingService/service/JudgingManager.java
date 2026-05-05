package com.maksim.testingService.service;

import com.maksim.common.enums.CheckerType;
import com.maksim.common.enums.Status;
import com.maksim.common.event.SolutionSubmittedEvent;
import com.maksim.testingService.exception.BadVerdictException;
import com.maksim.testingService.kafka.KafkaEventPublisher;
import com.maksim.testingService.service.execution.Checker;
import com.maksim.testingService.service.execution.Compiler;
import com.maksim.testingService.service.execution.Runner;
import com.maksim.testingService.service.model.TestCasesMetadata;
import com.maksim.testingService.service.model.VerdictInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class JudgingManager {
    private static final String SOURCE_FILE_NAME = "main";
    private static final String CONTESTANT_OUT_FILE_NAME = "output.out";
    private static final String CHECKER_OUT_FILE_NAME = "checker_.out";

    @Value("${judging.tests.dir}")
    private String testDir;

    @Value("${judging.sessions.dir}")
    private String sessionDir;

    private final KafkaEventPublisher kafkaEventPublisher;
    private final Compiler compiler;
    private final Runner runner;
    private final Checker checker;
    private final ObjectMapper objectMapper;

    public void judge(SolutionSubmittedEvent submissionMeta) {
        VerdictInfo verdictInfo = new VerdictInfo();

        try {
            log.debug("Starting process submission {}", submissionMeta.getSubmissionId());

            Path judgingSessionDir = Files.createTempDirectory(Path.of(sessionDir), null);
            Path sourceFile = Files.createFile(
                    judgingSessionDir.resolve(SOURCE_FILE_NAME + submissionMeta.getLanguage().sourceSuffix)
            );
            Files.writeString(sourceFile, submissionMeta.getSource());

            log.debug("Starting compilation of submission {}", submissionMeta.getSubmissionId());
            Path compiledFile = compiler.compile(sourceFile, judgingSessionDir, submissionMeta, verdictInfo);
            log.debug("Compilation of submission {} passed success", submissionMeta.getSubmissionId());

            Path contestantSolution = judgingSessionDir.resolve(CONTESTANT_OUT_FILE_NAME);
            Path checkerOutFile = judgingSessionDir.resolve(CHECKER_OUT_FILE_NAME);

            Path testSetDir = Path.of(testDir).resolve("problem_" + submissionMeta.getProblemId());
            TestCasesMetadata meta = objectMapper.readValue(
                    testSetDir.resolve("meta.json"),
                    TestCasesMetadata.class
            );

            for (int testNum = 1; testNum <= meta.getTestCount(); testNum++) {
                log.debug("Testing submission #{} on test #{}", submissionMeta.getSubmissionId(), testNum);
                verdictInfo.setTestNum(testNum);
                kafkaEventPublisher.sendProgressAsync(submissionMeta.getSubmissionId(), testNum);

                Path inputFile = testSetDir.resolve(testNum + ".in");
                Path jurySolution = testSetDir.resolve(testNum + ".out");

                runner.run(submissionMeta, compiledFile, inputFile, contestantSolution, verdictInfo);

                if (meta.getCheckerType() == CheckerType.DEFAULT_EXACT_MATCH_CHECKER) {
                    checker.exactMatchCheck(jurySolution, contestantSolution, verdictInfo);
                } else {
                    checker.customCheck(
                            testSetDir.resolve(meta.getCheckerFileName()),
                            meta.getCheckerLanguage(),
                            inputFile,
                            contestantSolution,
                            checkerOutFile,
                            verdictInfo
                    );
                }
            }

            verdictInfo.setCheckerMessage("OK");
            verdictInfo.setStatus(Status.OK);
        } catch (IOException ex) {
            log.error("Server error while judging submission #{}", submissionMeta.getSubmissionId());
            throw new RuntimeException(ex);
        } catch (BadVerdictException ex) {
            verdictInfo.setCheckerMessage(ex.getMessage());
        }

        log.debug(
                "Submission #{} has been tested successfully with verdict {}",
                submissionMeta.getSubmissionId(),
                verdictInfo.getStatus()
        );
        kafkaEventPublisher.sendVerdict(submissionMeta.getSubmissionId(), verdictInfo);
    }
}
