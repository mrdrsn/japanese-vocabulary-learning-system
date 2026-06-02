package com.example.japanesevocabularylearningsystem.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utterance implements Serializable {

    private String id;
    private String surfaceRomaji;
    private String translation;
    private boolean isFixedExpression;

    private String roleId;
    private String communicativeIntentId;

    private String type;
    private List<String> stepDisplayNames = new ArrayList<>();
    private List<String> roleDisplayNames = new ArrayList<>();
    private List<String> communicativeIntentNames = new ArrayList<>();
    private String audioUrl;
    private List<ExampleEntry> examples = new ArrayList<>();

    public static class ExampleEntry implements Serializable {
        public String luId;
        public String luRomaji;
        public String filledRomaji;
        public String luTranslation;
        public String luId2;      // второй слот, null для одно-слотовых шаблонов
        public String luRomaji2;

        public ExampleEntry(String luId, String luRomaji, String filledRomaji, String luTranslation) {
            this.luId = luId;
            this.luRomaji = luRomaji;
            this.filledRomaji = filledRomaji;
            this.luTranslation = luTranslation;
        }
    }

    public Utterance() {}

    public Utterance(String id, String surfaceRomaji, String translation, boolean isFixedExpression) {
        this.id = id;
        this.surfaceRomaji = surfaceRomaji;
        this.translation = translation;
        this.isFixedExpression = isFixedExpression;
    }

    public Utterance(String id, String surfaceRomaji, String translation,
                     boolean isFixedExpression, String roleId,
                     String communicativeIntentId, String stepDisplayName) {
        this.id = id;
        this.surfaceRomaji = surfaceRomaji;
        this.translation = translation;
        this.isFixedExpression = isFixedExpression;
        this.roleId = roleId;
        this.communicativeIntentId = communicativeIntentId;
        if (stepDisplayName != null)
            this.stepDisplayNames = new ArrayList<>(Collections.singletonList(stepDisplayName));
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSurfaceRomaji() { return surfaceRomaji; }
    public void setSurfaceRomaji(String v) { this.surfaceRomaji = v; }
    public String getTranslation() { return translation; }
    public void setTranslation(String v) { this.translation = v; }
    public boolean isFixedExpression() { return isFixedExpression; }
    public void setFixedExpression(boolean v) { this.isFixedExpression = v; }
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public String getCommunicativeIntentId() { return communicativeIntentId; }
    public void setCommunicativeIntentId(String v) { this.communicativeIntentId = v; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<String> getStepDisplayNames() { return stepDisplayNames; }
    public void setStepDisplayNames(List<String> v) { this.stepDisplayNames = v != null ? v : new ArrayList<>(); }
    public List<String> getRoleDisplayNames() { return roleDisplayNames; }
    public void setRoleDisplayNames(List<String> v) { this.roleDisplayNames = v != null ? v : new ArrayList<>(); }
    public List<String> getCommunicativeIntentNames() { return communicativeIntentNames; }
    public void setCommunicativeIntentNames(List<String> v) { this.communicativeIntentNames = v != null ? v : new ArrayList<>(); }
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    public List<ExampleEntry> getExamples() { return examples; }
    public void setExamples(List<ExampleEntry> v) { this.examples = v != null ? v : new ArrayList<>(); }
}