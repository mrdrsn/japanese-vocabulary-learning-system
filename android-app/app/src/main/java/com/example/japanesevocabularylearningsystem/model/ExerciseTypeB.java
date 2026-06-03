package com.example.japanesevocabularylearningsystem.model;

import java.io.Serializable;
import java.util.List;

public class ExerciseTypeB implements Serializable {
    private String instruction;
    private String audioUrl;
    private String roleName;
    private List<String> options;
    private List<Boolean> correctFlags;

    public ExerciseTypeB() {}

    public ExerciseTypeB(String instruction, String audioUrl, String roleName,
                         List<String> options, List<Boolean> correctFlags) {
        this.instruction = instruction;
        this.audioUrl = audioUrl;
        this.roleName = roleName;
        this.options = options;
        this.correctFlags = correctFlags;
    }

    public String getInstruction()         { return instruction; }
    public String getAudioUrl()            { return audioUrl; }
    public String getRoleName()            { return roleName; }
    public List<String> getOptions()       { return options; }
    public List<Boolean> getCorrectFlags() { return correctFlags; }
}