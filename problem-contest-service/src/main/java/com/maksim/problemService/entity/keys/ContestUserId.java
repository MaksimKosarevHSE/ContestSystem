package com.maksim.problemService.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class ContestUserId implements Serializable {
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "contest_id")
    private Integer contestId;

    public ContestUserId(Integer userId, Integer contestId) {
        this.userId = userId;
        this.contestId = contestId;
    }

    public ContestUserId() {

    }
}
