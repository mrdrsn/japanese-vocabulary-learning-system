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

    // поля для mock/тренировочного режима
    private String roleId;
    private String communicativeIntentId;

    // поля из API (List для поддержки нескольких значений)
    private String type;
    private List<String> stepDisplayNames = new ArrayList<>();
    private List<String> roleDisplayNames = new ArrayList<>();
    private List<String> communicativeIntentNames = new ArrayList<>();
    private String audioUrl;

    public Utterance() {}

    // 4-аргументный конструктор (старый, для совместимости)
    public Utterance(String id, String surfaceRomaji, String translation, boolean isFixedExpression) {
        this.id = id;
        this.surfaceRomaji = surfaceRomaji;
        this.translation = translation;
        this.isFixedExpression = isFixedExpression;
    }

    // 7-аргументный конструктор (для MockDataProvider и CardLearnActivity)
    public Utterance(String id, String surfaceRomaji, String translation,
                     boolean isFixedExpression, String roleId,
                     String communicativeIntentId, String stepDisplayName) {
        this.id = id;
        this.surfaceRomaji = surfaceRomaji;
        this.translation = translation;
        this.isFixedExpression = isFixedExpression;
        this.roleId = roleId;
        this.communicativeIntentId = communicativeIntentId;
        if (stepDisplayName != null) {
            this.stepDisplayNames = new ArrayList<>(Collections.singletonList(stepDisplayName));
        }
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
    public void setStepDisplayNames(List<String> v) {
        this.stepDisplayNames = v != null ? v : new ArrayList<>();
    }

    public List<String> getRoleDisplayNames() { return roleDisplayNames; }
    public void setRoleDisplayNames(List<String> v) {
        this.roleDisplayNames = v != null ? v : new ArrayList<>();
    }

    public List<String> getCommunicativeIntentNames() { return communicativeIntentNames; }
    public void setCommunicativeIntentNames(List<String> v) {
        this.communicativeIntentNames = v != null ? v : new ArrayList<>();
    }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
}