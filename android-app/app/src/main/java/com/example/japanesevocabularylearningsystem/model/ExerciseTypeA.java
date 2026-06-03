package com.example.japanesevocabularylearningsystem.model;

import java.io.Serializable;
import java.util.List;

public class ExerciseTypeA implements Serializable {

    private String instruction;
    private String translation;
    private String romajiWithGap;
    private String roleName;
    private List<String> options;
    private List<Boolean> correctFlags;

    public ExerciseTypeA() {}

    public ExerciseTypeA(String instruction, String translation, String romajiWithGap,
                         String roleName, List<String> options, List<Boolean> correctFlags) {
        this.instruction = instruction;
        this.translation = translation;
        this.romajiWithGap = romajiWithGap;
        this.roleName = roleName;
        this.options = options;
        this.correctFlags = correctFlags;
    }

    public String getInstruction()       { return instruction; }
    public String getTranslation()       { return translation; }
    public String getRomajiWithGap()     { return romajiWithGap; }
    public String getRoleName()          { return roleName; }
    public List<String> getOptions()     { return options; }
    public List<Boolean> getCorrectFlags() { return correctFlags; }
}