package com.example.japanesevocabularylearningsystem.network.dto;

public class TrainingSessionSaveDto {
    public String scenarioId;
    public int correctCount;
    public int totalCount;

    public TrainingSessionSaveDto(String scenarioId, int correctCount, int totalCount) {
        this.scenarioId = scenarioId;
        this.correctCount = correctCount;
        this.totalCount = totalCount;
    }
}