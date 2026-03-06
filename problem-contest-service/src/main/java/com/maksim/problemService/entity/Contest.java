package com.maksim.problemService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contests")
@Data
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private int id;
    @Column(name = "title")
    private String title;
    @Column(name = "author_id")
    private int authorId;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @OneToMany(mappedBy = "contest")
    private List<ContestProblem> problems;

    public List<Problem> getProblems(){
        return problems.stream().map(el -> el.getProblem()).toList();
    }
}
