package com.maksim.testingService.service;

import com.maksim.testingService.enums.CheckerType;
import com.maksim.testingService.enums.ProgrammingLanguage;
import com.maksim.testingService.enums.Status;
import com.maksim.testingService.event.TestCaseJudgedEvent;
import com.maksim.testingService.event.SolutionSubmittedEvent;
import com.maksim.testingService.exception.*;
import com.maksim.testingService.kafka.KafkaEventPublisher;
import com.maksim.testingService.service.execution.Checker;
import com.maksim.testingService.service.execution.Compiler;
import com.maksim.testingService.service.execution.Runner;
import com.maksim.testingService.service.model.TestCasesMetadata;
import com.maksim.testingService.service.model.VerdictInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JudgingManager {

    @Value("${judging.tests.dir}")
    private String testDir;

    @Value("${judging.sessions.dir}")
    private String sessionDir;

    private final String SOURCE_FILE_NAME = "main";

    private final String CONTESTANT_OUT_FILE_NAME = "output.out";

    private final String CHECKER_OUT_FILE_NAME = "checker_.out";

    private final KafkaEventPublisher kafkaEventPublisher;

    private final Compiler compiler;

    private final Runner runner;

    private final Checker checker;

    public void judge(SolutionSubmittedEvent submissionMeta) {

        VerdictInfo verdictInfo = new VerdictInfo();

        try {
            log.debug("Starting process submission {}", submissionMeta.submissionId());
            Path sessionDir = Files.createTempDirectory(Path.of(this.sessionDir), null);
            Path sourceFile = Files.createFile(sessionDir.resolve(SOURCE_FILE_NAME + submissionMeta.language().sourceSuffix));
            Files.writeString(sourceFile, submissionMeta.source());

            log.debug("Starting compilation of submission {}", submissionMeta.submissionId());
            Path compiledFile = compiler.compile(sourceFile, sessionDir, submissionMeta, verdictInfo);
            log.debug("Compilation of submission {} passed success", submissionMeta.submissionId());

            Path contestantSolution = sessionDir.resolve(CONTESTANT_OUT_FILE_NAME);
            Path checkerOutFile = sessionDir.resolve(CHECKER_OUT_FILE_NAME);

            Path testSetDir = Path.of(testDir).resolve("problem_" + submissionMeta.problemId());
            TestCasesMetadata meta = new ObjectMapper().readValue(testSetDir.resolve("meta.json"), TestCasesMetadata.class);
            int testsCnt = meta.getTestCount();

            for (int i = 1; i <= testsCnt; i++) {
                log.debug("Testing submission #{} on test №{}", submissionMeta.submissionId(), i);
                verdictInfo.setTestNum(i);
                kafkaEventPublisher.sendProgressAsync(submissionMeta.submissionId(), i);

                Path inputFile = testSetDir.resolve(i + ".in");

                runner.run(submissionMeta, compiledFile, inputFile, contestantSolution, verdictInfo);

                Path jurySolution = testSetDir.resolve(i + ".out");

                if (meta.getCheckerType() == CheckerType.DEFAULT_EXACT_MATCH_CHECKER) {
                    checker.exactMatchCheck(jurySolution, contestantSolution, verdictInfo);
                } else {
                    checker.customCheck(testSetDir.resolve(meta.getCheckerFileName()),
                            meta.getCheckerLanguage(),
                            inputFile,
                            contestantSolution,
                            checkerOutFile,
                            verdictInfo);
                }
            }

            verdictInfo.setCheckerMessage("OK");
            verdictInfo.setStatus(Status.OK);

        } catch (IOException ex) {
            log.error("Server error while judging submission №{}", submissionMeta.submissionId());
            throw new RuntimeException(ex);
        } catch (BadVerdictException ex) {
            verdictInfo.setCheckerMessage(ex.getMessage());
        }
        log.debug("Submission №{} has been tested successfully with verdict {}", submissionMeta.submissionId(), verdictInfo.getStatus());
        kafkaEventPublisher.sendVerdict(submissionMeta.submissionId(), verdictInfo);
    }
}




