package com.example.japanesevocabularylearningsystem.model;

import java.util.List;

public class ExerciseTypeD {

    private String promptUtteranceId;
    private List<String> saidOptions;
    private int correctSaidIndex;
    private List<String> responseOptions;
    private int correctResponseIndex;

    public ExerciseTypeD(String promptUtteranceId,
                         List<String> saidOptions, int correctSaidIndex,
                         List<String> responseOptions, int correctResponseIndex) {
        this.promptUtteranceId = promptUtteranceId;
        this.saidOptions = saidOptions;
        this.correctSaidIndex = correctSaidIndex;
        this.responseOptions = responseOptions;
        this.correctResponseIndex = correctResponseIndex;
    }

    public String getPromptUtteranceId() { return promptUtteranceId; }
    public List<String> getSaidOptions() { return saidOptions; }
    public int getCorrectSaidIndex() { return correctSaidIndex; }
    public List<String> getResponseOptions() { return responseOptions; }
    public int getCorrectResponseIndex() { return correctResponseIndex; }
}