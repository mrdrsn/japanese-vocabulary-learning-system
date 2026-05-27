package com.example.japanesevocabularylearningsystem.model;


public class Utterance {
    private String id;
    private String surfaceRomaji;
    private String translation;
    private boolean isFixedExpression;
    private String roleId;
    private String communicativeIntentId;
    private String stepName;

    public Utterance() {
    }

    public Utterance(String id, String surfaceRomaji, String translation, boolean isFixedExpression) {
        this.id = id;
        this.surfaceRomaji = surfaceRomaji;
        this.translation = translation;
        this.isFixedExpression = isFixedExpression;
    }

    public Utterance(String id, String surfaceRomaji, String translation,
                     boolean isFixedExpression,
                     String roleId, String communicativeIntentId, String stepName) {
        this.id = id;
        this.surfaceRomaji = surfaceRomaji;
        this.translation = translation;
        this.isFixedExpression = isFixedExpression;
        this.roleId = roleId;
        this.communicativeIntentId = communicativeIntentId;
        this.stepName = stepName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSurfaceRomaji() {
        return surfaceRomaji;
    }

    public void setSurfaceRomaji(String surfaceRomaji) {
        this.surfaceRomaji = surfaceRomaji;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }

    public String getCommunicativeIntentId() { return communicativeIntentId; }
    public void setCommunicativeIntentId(String communicativeIntentId) {
        this.communicativeIntentId = communicativeIntentId;
    }
    public boolean isFixedExpression() {
        return isFixedExpression;
    }

    public void setFixedExpression(boolean fixedExpression) {
        isFixedExpression = fixedExpression;
    }

    @Override
    public String toString() {
        return "Utterance{" +
                "id='" + id + '\'' +
                ", surfaceRomaji='" + surfaceRomaji + '\'' +
                ", translation='" + translation + '\'' +
                ", isFixedExpression=" + isFixedExpression +
                '}';
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }
}