package com.maksim.problemService.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ContestUserId that = (ContestUserId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(contestId, that.contestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, contestId);
    }
}
