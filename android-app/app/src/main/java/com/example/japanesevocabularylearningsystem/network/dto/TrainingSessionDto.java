package com.example.japanesevocabularylearningsystem.network.dto;

public class TrainingSessionDto {
    public Long id;
    public String scenarioId;
    public String finishedAt;             // "dd.MM.yyyy HH:mm"
    public int correctCount;
    public int totalCount;
    public Integer previousCorrectCount;  // null если первая тренировка
}