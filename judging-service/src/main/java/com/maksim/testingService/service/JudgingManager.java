package com.maksim.testingService.service;

import com.maksim.common.enums.CheckerType;
import com.maksim.common.enums.ProgrammingLanguage;
import com.maksim.common.enums.Status;
import com.maksim.common.event.SolutionJudgedEvent;
import com.maksim.common.event.SolutionSubmittedEvent;
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
            log.debug("Starting process submission {}", submissionMeta.getSubmissionId());
            Path sessionDir = Files.createTempDirectory(Path.of(this.sessionDir), null);
            Path sourceFile = Files.createFile(sessionDir.resolve(SOURCE_FILE_NAME + submissionMeta.getLanguage().sourceSuffix));
            Files.writeString(sourceFile, submissionMeta.getSource());

            log.debug("Starting compilation of submission {}", submissionMeta.getSubmissionId());
            Path compiledFile = compiler.compile(sourceFile, sessionDir, submissionMeta, verdictInfo);
            log.debug("Compilation of submission {} passed success", submissionMeta.getSubmissionId());

            Path contestantSolution = sessionDir.resolve(CONTESTANT_OUT_FILE_NAME);
            Path checkerOutFile = sessionDir.resolve(CHECKER_OUT_FILE_NAME);

            Path testSetDir = Path.of(testDir).resolve("problem_" + submissionMeta.getProblemId());
            TestCasesMetadata meta = new ObjectMapper().readValue(testSetDir.resolve("meta.json"), TestCasesMetadata.class);
            int testsCnt = meta.getTestCount();

            for (int i = 1; i <= testsCnt; i++) {
                log.debug("Testing submission #{} on test №{}", submissionMeta.getSubmissionId(), i);
                verdictInfo.setTestNum(i);
                kafkaEventPublisher.sendProgressAsync(submissionMeta.getSubmissionId(), i);

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
            log.error("Server error while judging submission №{}", submissionMeta.getSubmissionId());
            throw new RuntimeException(ex);
        } catch (BadVerdictException ex) {
            verdictInfo.setCheckerMessage(ex.getMessage());
        }
        log.debug("Submission №{} has been tested successfully with verdict {}", submissionMeta.getSubmissionId(), verdictInfo.getStatus());
        kafkaEventPublisher.sendVerdict(submissionMeta.getSubmissionId(), verdictInfo);
    }
}
