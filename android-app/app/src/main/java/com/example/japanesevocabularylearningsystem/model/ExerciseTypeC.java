package com.example.japanesevocabularylearningsystem.model;

import java.util.List;

public class ExerciseTypeC {

    private String utteranceId;
    private String correctRoleId;
    private List<String> translationOptions;
    private int correctTranslationIndex;

    public ExerciseTypeC(String utteranceId, String correctRoleId,
                         List<String> translationOptions, int correctTranslationIndex) {
        this.utteranceId = utteranceId;
        this.correctRoleId = correctRoleId;
        this.translationOptions = translationOptions;
        this.correctTranslationIndex = correctTranslationIndex;
    }

    public String getUtteranceId() { return utteranceId; }
    public String getCorrectRoleId() { return correctRoleId; }
    public List<String> getTranslationOptions() { return translationOptions; }
    public int getCorrectTranslationIndex() { return correctTranslationIndex; }
}