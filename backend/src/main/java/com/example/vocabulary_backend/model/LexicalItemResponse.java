package com.example.vocabulary_backend.model;

import java.util.ArrayList;
import java.util.List;

public class LexicalItemResponse {

    private String id;
    private String type;
    private String romaji;
    private String translation;
    private List<String> roleDisplayNames = new ArrayList<>();
    private List<String> stepDisplayNames = new ArrayList<>();
    private List<String> communicativeIntentNames = new ArrayList<>();
    private String audioUrl;
    private List<ExampleEntry> examples = new ArrayList<>();

    public static class ExampleEntry {
        private String luId;
        private String luRomaji;
        private String filledRomaji;
        private String luTranslation;
        private String luId2, luRomaji2;

        public ExampleEntry(String luId, String luRomaji, String filledRomaji, String luTranslation) {
            this.luId = luId;
            this.luRomaji = luRomaji;
            this.filledRomaji = filledRomaji;
            this.luTranslation = luTranslation;
        }

        public String getLuId() { return luId; }
        public String getLuRomaji() { return luRomaji; }
        public String getFilledRomaji() { return filledRomaji; }
        public String getLuTranslation() { return luTranslation; }
        public String getLuId2()    { return luId2; }
        public void setLuId2(String luId2) { this.luId2 = luId2; }
        public String getLuRomaji2() { return luRomaji2; }
        public void setLuRomaji2(String luRomaji2) { this.luRomaji2 = luRomaji2; }
    }

    public LexicalItemResponse() {}

    public LexicalItemResponse(String id, String type, String romaji, String translation) {
        this.id = id;
        this.type = type;
        this.romaji = romaji;
        this.translation = translation;
    }

    public void addStep(String step) {
        if (step != null && !stepDisplayNames.contains(step)) stepDisplayNames.add(step);
    }

    public void addRoles(List<String> roles) {
        for (String r : roles)
            if (r != null && !roleDisplayNames.contains(r)) roleDisplayNames.add(r);
    }

    public void addIntent(String intent) {
        if (intent != null && !communicativeIntentNames.contains(intent))
            communicativeIntentNames.add(intent);
    }

    public void addExample(ExampleEntry entry) {
        boolean exists = examples.stream()
                .anyMatch(e -> e.getFilledRomaji() != null
                        && e.getFilledRomaji().equals(entry.getFilledRomaji()));
        if (!exists) examples.add(entry);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRomaji() { return romaji; }
    public void setRomaji(String romaji) { this.romaji = romaji; }
    public String getTranslation() { return translation; }
    public void setTranslation(String translation) { this.translation = translation; }
    public List<String> getRoleDisplayNames() { return roleDisplayNames; }
    public void setRoleDisplayNames(List<String> v) { this.roleDisplayNames = v; }
    public List<String> getStepDisplayNames() { return stepDisplayNames; }
    public void setStepDisplayNames(List<String> v) { this.stepDisplayNames = v; }
    public List<String> getCommunicativeIntentNames() { return communicativeIntentNames; }
    public void setCommunicativeIntentNames(List<String> v) { this.communicativeIntentNames = v; }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public List<ExampleEntry> getExamples() { return examples; }
    public void setExamples(List<ExampleEntry> examples) { this.examples = examples; }
}