package com.example.japanesevocabularylearningsystem.model;

import java.util.List;

public class ExerciseTypeB {

    private String utteranceId;
    private String roleId;
    private List<String> options;
    private int correctOptionIndex;

    public ExerciseTypeB(String utteranceId, String roleId,
                         List<String> options, int correctOptionIndex) {
        this.utteranceId = utteranceId;
        this.roleId = roleId;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getUtteranceId() { return utteranceId; }
    public String getRoleId() { return roleId; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
}