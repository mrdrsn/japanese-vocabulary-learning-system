package com.example.japanesevocabularylearningsystem.network.dto;

import com.example.japanesevocabularylearningsystem.model.ExerciseTypeB;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTypeBDto {
    public String utteranceId;
    public String audioUrl;
    public String correctTranslation;
    public String roleName;
    public List<OptionDto> options;

    public static class OptionDto {
        public String translation;
        public boolean correct;
    }

    public ExerciseTypeB toModel() {
        List<String> optionTexts = new ArrayList<>();
        List<Boolean> correctFlags = new ArrayList<>();
        if (options != null) {
            for (OptionDto opt : options) {
                optionTexts.add(opt.translation != null ? opt.translation : "");
                correctFlags.add(opt.correct);
            }
        }
        // Genitive for Russian: Кассир→кассира, Официант→официанта
        String rn = roleName != null ? roleName : "";
        String instruction = "Переведите фразу " + rn.toLowerCase() + "а";
        return new ExerciseTypeB(instruction, audioUrl, rn, optionTexts, correctFlags);
    }
}