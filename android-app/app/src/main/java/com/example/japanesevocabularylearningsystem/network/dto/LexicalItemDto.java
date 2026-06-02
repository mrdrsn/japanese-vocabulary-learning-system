package com.example.japanesevocabularylearningsystem.network.dto;

import java.util.List;

public class LexicalItemDto {
    public String id;
    public String type;
    public String romaji;
    public String translation;
    public List<String> roleDisplayNames;
    public List<String> stepDisplayNames;
    public List<String> communicativeIntentNames;
    public String audioUrl;
    public List<ExampleEntryDto> examples;

    public static class ExampleEntryDto {
        public String luId;
        public String luRomaji;
        public String filledRomaji;
        public String luTranslation;
        public String luId2, luRomaji2;
    }
}