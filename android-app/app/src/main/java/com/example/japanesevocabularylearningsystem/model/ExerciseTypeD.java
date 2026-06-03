package com.example.japanesevocabularylearningsystem.model;

import java.io.Serializable;
import java.util.List;

public class ExerciseTypeD implements Serializable {
    private String instruction;
    private String situationYou;
    private String situationStep;
    private String situationIntent;
    private List<String> options;
    private List<Boolean> correctFlags;

    public ExerciseTypeD() {}

    public ExerciseTypeD(String instruction, String situationYou, String situationStep,
                         String situationIntent, List<String> options, List<Boolean> correctFlags) {
        this.instruction = instruction;
        this.situationYou = situationYou;
        this.situationStep = situationStep;
        this.situationIntent = situationIntent;
        this.options = options;
        this.correctFlags = correctFlags;
    }

    public String getInstruction()         { return instruction; }
    public String getSituationYou()        { return situationYou; }
    public String getSituationStep()       { return situationStep; }
    public String getSituationIntent()     { return situationIntent; }
    public List<String> getOptions()       { return options; }
    public List<Boolean> getCorrectFlags() { return correctFlags; }
}