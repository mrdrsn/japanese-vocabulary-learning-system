package com.example.japanesevocabularylearningsystem.model;

import java.util.List;

public class ExerciseTypeA {

    private String instruction;
    private String translation;
    private String romajiWithGap;
    private String roleId;
    private List<String> options;
    private int correctOptionIndex;

    public ExerciseTypeA() {}

    public ExerciseTypeA(String instruction, String translation, String romajiWithGap,
                         String roleId, List<String> options, int correctOptionIndex) {
        this.instruction = instruction;
        this.translation = translation;
        this.romajiWithGap = romajiWithGap;
        this.roleId = roleId;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getInstruction() { return instruction; }
    public String getTranslation() { return translation; }
    public String getRomajiWithGap() { return romajiWithGap; }
    public String getRoleId() { return roleId; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
}