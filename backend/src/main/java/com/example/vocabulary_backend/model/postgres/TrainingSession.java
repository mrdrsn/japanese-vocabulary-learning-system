package com.example.vocabulary_backend.model.postgres;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "training_session")
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scenario_id", nullable = false, length = 50)
    private String scenarioId;

    @Column(name = "finished_at", nullable = false)
    private OffsetDateTime finishedAt;

    @Column(name = "correct_count", nullable = false)
    private int correctCount;

    @Column(name = "total_count", nullable = false)
    private int totalCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getScenarioId() { return scenarioId; }
    public void setScenarioId(String s) { this.scenarioId = s; }
    public OffsetDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(OffsetDateTime t) { this.finishedAt = t; }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int n) { this.correctCount = n; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int n) { this.totalCount = n; }
}