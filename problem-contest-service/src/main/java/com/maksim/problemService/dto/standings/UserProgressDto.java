package com.maksim.problemService.dto.standings;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UserProgressDto {
    private int userId;
    int place;
//    private int fine;
    List<TaskProgressDto> taskProgress;
    private int score;
}
