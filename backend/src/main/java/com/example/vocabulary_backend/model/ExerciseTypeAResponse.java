package com.example.vocabulary_backend.model;

import java.util.List;

public class ExerciseTypeAResponse {

    private String romajiWithGap;
    private String translation;
    private String roleName;
    private List<OptionDto> options;

    public ExerciseTypeAResponse(String romajiWithGap, String translation,
                                 String roleName, List<OptionDto> options) {
        this.romajiWithGap = romajiWithGap;
        this.translation = translation;
        this.roleName = roleName;
        this.options = options;
    }

    public String getRomajiWithGap() { return romajiWithGap; }
    public String getTranslation()   { return translation; }
    public String getRoleName()      { return roleName; }
    public List<OptionDto> getOptions() { return options; }

    public static class OptionDto {
        private String luId;
        private String romaji;
        private boolean correct;

        public OptionDto(String luId, String romaji, boolean correct) {
            this.luId = luId;
            this.romaji = romaji;
            this.correct = correct;
        }

        public String getLuId()   { return luId; }
        public String getRomaji() { return romaji; }
        public boolean isCorrect() { return correct; }
    }
}