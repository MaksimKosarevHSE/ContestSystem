package com.maksim.problemService.entity;

import com.maksim.problemService.entity.associative.ContestProblem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "author_id")
    private Integer authorId;
    @Column(name = "start_time")
    private Instant startTime;
    @Column(name = "end_time")
    private Instant endTime;
    @OneToMany(mappedBy = "contest", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<ContestProblem> problems = new ArrayList<>();

    public List<Problem> getProblemList() {
        return problems.stream().map(ContestProblem::getProblem).toList();
    }
}
