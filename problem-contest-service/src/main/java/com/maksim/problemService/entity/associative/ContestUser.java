package com.maksim.problemService.entity.associative;

import com.maksim.problemService.entity.Contest;
import com.maksim.problemService.entity.keys.ContestUserId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "contest_user")
@Entity
@Data
@NoArgsConstructor
public class ContestUser {
    @EmbeddedId
    private ContestUserId id;

    @ManyToOne
    @MapsId("contestId")
    @JoinColumn(name = "contest_id")
    private Contest contest;

    @Column(name = "total_score")
    private int totalScore;

    public ContestUser(ContestUserId cuId) {
        this.id = cuId;
    }

    public void addScore(int x) {
        totalScore += x;
    }


}
