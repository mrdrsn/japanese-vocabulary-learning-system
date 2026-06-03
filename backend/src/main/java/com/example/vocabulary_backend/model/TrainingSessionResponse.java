package com.example.vocabulary_backend.model;

public class TrainingSessionResponse {
    private Long id;
    private String scenarioId;
    private String finishedAt;       // "dd.MM.yyyy HH:mm"
    private int correctCount;
    private int totalCount;
    private Integer previousCorrectCount; // null если первая тренировка

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getScenarioId() { return scenarioId; }
    public void setScenarioId(String s) { this.scenarioId = s; }
    public String getFinishedAt() { return finishedAt; }
    public void setFinishedAt(String s) { this.finishedAt = s; }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int n) { this.correctCount = n; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int n) { this.totalCount = n; }
    public Integer getPreviousCorrectCount() { return previousCorrectCount; }
    public void setPreviousCorrectCount(Integer n) { this.previousCorrectCount = n; }
}