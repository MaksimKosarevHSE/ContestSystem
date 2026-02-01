package com.maksim.submissionAcceptorService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProblemDto {
    private int id;
    private double compileTimeLimit;
    private double timeLimit;
    private double memoryLimit;
}
