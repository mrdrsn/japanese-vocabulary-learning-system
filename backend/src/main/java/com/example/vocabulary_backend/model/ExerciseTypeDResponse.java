package com.example.vocabulary_backend.model;

import java.util.List;

public class ExerciseTypeDResponse {
    private String situationYou;
    private String situationStep;
    private String situationIntent;
    private List<OptionDto> options;

    public ExerciseTypeDResponse(String situationYou, String situationStep,
                                 String situationIntent, List<OptionDto> options) {
        this.situationYou = situationYou;
        this.situationStep = situationStep;
        this.situationIntent = situationIntent;
        this.options = options;
    }

    public String getSituationYou()     { return situationYou; }
    public String getSituationStep()    { return situationStep; }
    public String getSituationIntent()  { return situationIntent; }
    public List<OptionDto> getOptions() { return options; }

    public static class OptionDto {
        private String romaji;
        private boolean correct;

        public OptionDto(String romaji, boolean correct) {
            this.romaji = romaji;
            this.correct = correct;
        }

        public String getRomaji()  { return romaji; }
        public boolean isCorrect() { return correct; }
    }
}