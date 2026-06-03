package com.example.vocabulary_backend.model;

public class TrainingSessionRequest {
    private String scenarioId;
    private int correctCount;
    private int totalCount;

    public String getScenarioId() { return scenarioId; }
    public void setScenarioId(String s) { this.scenarioId = s; }
    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int n) { this.correctCount = n; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int n) { this.totalCount = n; }
}