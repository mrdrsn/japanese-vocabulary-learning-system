package com.example.japanesevocabularylearningsystem.model;

import java.io.Serializable;
import java.util.List;

public class ExerciseTypeC implements Serializable {
    private String instruction;
    private String audioUrl;
    private List<String> roleOptions;
    private List<Boolean> roleCorrectFlags;
    private List<String> translationOptions;
    private List<Boolean> translationCorrectFlags;

    public ExerciseTypeC() {}

    public ExerciseTypeC(String instruction, String audioUrl,
                         List<String> roleOptions, List<Boolean> roleCorrectFlags,
                         List<String> translationOptions, List<Boolean> translationCorrectFlags) {
        this.instruction = instruction;
        this.audioUrl = audioUrl;
        this.roleOptions = roleOptions;
        this.roleCorrectFlags = roleCorrectFlags;
        this.translationOptions = translationOptions;
        this.translationCorrectFlags = translationCorrectFlags;
    }

    public String getInstruction()                    { return instruction; }
    public String getAudioUrl()                       { return audioUrl; }
    public List<String> getRoleOptions()              { return roleOptions; }
    public List<Boolean> getRoleCorrectFlags()        { return roleCorrectFlags; }
    public List<String> getTranslationOptions()       { return translationOptions; }
    public List<Boolean> getTranslationCorrectFlags() { return translationCorrectFlags; }
}