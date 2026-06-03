package com.example.vocabulary_backend.model;

import java.util.List;

public class ExerciseTypeBResponse {
    private String utteranceId;
    private String audioUrl;
    private String correctTranslation;
    private String roleName;
    private List<OptionDto> options;

    public ExerciseTypeBResponse(String utteranceId, String audioUrl,
                                 String correctTranslation, String roleName,
                                 List<OptionDto> options) {
        this.utteranceId = utteranceId;
        this.audioUrl = audioUrl;
        this.correctTranslation = correctTranslation;
        this.roleName = roleName;
        this.options = options;
    }

    public String getUtteranceId()       { return utteranceId; }
    public String getAudioUrl()          { return audioUrl; }
    public String getCorrectTranslation(){ return correctTranslation; }
    public String getRoleName()          { return roleName; }
    public List<OptionDto> getOptions()  { return options; }

    public static class OptionDto {
        private String translation;
        private boolean correct;

        public OptionDto(String translation, boolean correct) {
            this.translation = translation;
            this.correct = correct;
        }

        public String getTranslation() { return translation; }
        public boolean isCorrect()     { return correct; }
    }
}