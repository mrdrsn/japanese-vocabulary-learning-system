package com.example.japanesevocabularylearningsystem.network.dto;

import com.example.japanesevocabularylearningsystem.model.ExerciseTypeA;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTypeADto {

    public String romajiWithGap;
    public String translation;
    public String roleName;
    public List<OptionDto> options;

    public static class OptionDto {
        public String luId;
        public String romaji;
        public boolean correct;
    }

    public ExerciseTypeA toModel() {
        List<String> optionTexts = new ArrayList<>();
        List<Boolean> correctFlags = new ArrayList<>();
        if (options != null) {
            for (OptionDto opt : options) {
                optionTexts.add(opt.romaji != null ? opt.romaji : "");
                correctFlags.add(opt.correct);
            }
        }
        String instruction = "Закончите фразу...";
        return new ExerciseTypeA(instruction, translation, romajiWithGap,
                roleName, optionTexts, correctFlags);
    }
}