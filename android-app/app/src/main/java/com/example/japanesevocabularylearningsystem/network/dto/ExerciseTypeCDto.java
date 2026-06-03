package com.example.japanesevocabularylearningsystem.network.dto;

import com.example.japanesevocabularylearningsystem.model.ExerciseTypeC;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTypeCDto {
    public String utteranceId;
    public String audioUrl;
    public List<RoleOptionDto> roles;
    public List<TranslationOptionDto> translations;

    public static class RoleOptionDto {
        public String displayName;
        public boolean correct;
    }

    public static class TranslationOptionDto {
        public String translation;
        public boolean correct;
    }

    public ExerciseTypeC toModel() {
        List<String> roleNames = new ArrayList<>();
        List<Boolean> roleFlags = new ArrayList<>();
        if (roles != null) {
            for (RoleOptionDto r : roles) {
                roleNames.add(r.displayName != null ? r.displayName : "");
                roleFlags.add(r.correct);
            }
        }
        List<String> transTexts = new ArrayList<>();
        List<Boolean> transFlags = new ArrayList<>();
        if (translations != null) {
            for (TranslationOptionDto t : translations) {
                transTexts.add(t.translation != null ? t.translation : "");
                transFlags.add(t.correct);
            }
        }
        return new ExerciseTypeC(
                "Укажите роль и переведите предложение",
                audioUrl, roleNames, roleFlags, transTexts, transFlags);
    }
}