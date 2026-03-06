package com.maksim.problemService.dto.standings;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskProgressDto {
    private int taskId;
    private boolean isSolved;
    private int attempts;
    private int secondsAfterSolving;
    private int score;
//    private int fine;
}
