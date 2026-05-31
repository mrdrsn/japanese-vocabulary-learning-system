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

    public LexicalItemResponse() {}

    public LexicalItemResponse(String id, String type, String romaji, String translation) {
        this.id = id;
        this.type = type;
        this.romaji = romaji;
        this.translation = translation;
    }

    public void addStep(String step) {
        if (step != null && !stepDisplayNames.contains(step)) {
            stepDisplayNames.add(step);
        }
    }

    public void addRoles(List<String> roles) {
        for (String r : roles) {
            if (r != null && !roleDisplayNames.contains(r)) {
                roleDisplayNames.add(r);
            }
        }
    }

    public void addIntent(String intent) {
        if (intent != null && !communicativeIntentNames.contains(intent)) {
            communicativeIntentNames.add(intent);
        }
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
    public void setRoleDisplayNames(List<String> roleDisplayNames) { this.roleDisplayNames = roleDisplayNames; }

    public List<String> getStepDisplayNames() { return stepDisplayNames; }
    public void setStepDisplayNames(List<String> stepDisplayNames) { this.stepDisplayNames = stepDisplayNames; }

    public List<String> getCommunicativeIntentNames() { return communicativeIntentNames; }
    public void setCommunicativeIntentNames(List<String> communicativeIntentNames) { this.communicativeIntentNames = communicativeIntentNames; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}