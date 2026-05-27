package com.example.japanesevocabularylearningsystem.model;

import java.util.List;

public class ExerciseTypeA {

    private String instruction;
    private String translation;
    private String romajiWithGap;
    private List<String> options;

    public ExerciseTypeA() {
    }

    public ExerciseTypeA(String instruction,
                         String translation,
                         String romajiWithGap,
                         List<String> options) {
        this.instruction = instruction;
        this.translation = translation;
        this.romajiWithGap = romajiWithGap;
        this.options = options;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getTranslation() {
        return translation;
    }

    public String getRomajiWithGap() {
        return romajiWithGap;
    }

    public List<String> getOptions() {
        return options;
    }
}