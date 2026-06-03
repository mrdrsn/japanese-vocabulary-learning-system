package com.example.vocabulary_backend.model;

import java.util.List;

public class ExerciseTypeCResponse {
    private String utteranceId;
    private String audioUrl;
    private List<RoleOptionDto> roles;
    private List<TranslationOptionDto> translations;

    public ExerciseTypeCResponse(String utteranceId, String audioUrl,
                                 List<RoleOptionDto> roles,
                                 List<TranslationOptionDto> translations) {
        this.utteranceId = utteranceId;
        this.audioUrl = audioUrl;
        this.roles = roles;
        this.translations = translations;
    }

    public String getUtteranceId()                       { return utteranceId; }
    public String getAudioUrl()                          { return audioUrl; }
    public List<RoleOptionDto> getRoles()                { return roles; }
    public List<TranslationOptionDto> getTranslations()  { return translations; }

    public static class RoleOptionDto {
        private String displayName;
        private boolean correct;

        public RoleOptionDto(String displayName, boolean correct) {
            this.displayName = displayName;
            this.correct = correct;
        }

        public String getDisplayName() { return displayName; }
        public boolean isCorrect()     { return correct; }
    }

    public static class TranslationOptionDto {
        private String translation;
        private boolean correct;

        public TranslationOptionDto(String translation, boolean correct) {
            this.translation = translation;
            this.correct = correct;
        }

        public String getTranslation() { return translation; }
        public boolean isCorrect()     { return correct; }
    }
}