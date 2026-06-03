package com.example.japanesevocabularylearningsystem.network.dto;

import com.example.japanesevocabularylearningsystem.model.ExerciseTypeD;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTypeDDto {
    public String situationYou;
    public String situationStep;
    public String situationIntent;
    public List<OptionDto> options;

    public static class OptionDto {
        public String romaji;
        public boolean correct;
    }

    public ExerciseTypeD toModel() {
        List<String> optionTexts = new ArrayList<>();
        List<Boolean> correctFlags = new ArrayList<>();
        if (options != null) {
            for (OptionDto opt : options) {
                optionTexts.add(opt.romaji != null ? opt.romaji : "");
                correctFlags.add(opt.correct);
            }
        }
        return new ExerciseTypeD("Подходящий ответ",
                situationYou, situationStep, situationIntent, optionTexts, correctFlags);
    }
}