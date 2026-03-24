    package com.maksim.submissionAcceptorService.event;

    import com.maksim.submissionAcceptorService.enums.Status;
    import lombok.*;

    @Data
    public class SubmissionJudgingProgressEvent {
        private Long submissionId;
        private Status status;
        private Integer testNum;
        // if status != TESTING else null
        private Integer memory;
        private Integer executionTime;
        private String checkerMessage;
    }
