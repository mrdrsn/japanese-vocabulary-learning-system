package com.example.japanesevocabularylearningsystem.model;


public class Utterance {
    private String id;
    private String surfaceRomaji;
    private String translation;
    private boolean isFixedExpression;

    public Utterance() {
    }

    public Utterance(String id, String surfaceRomaji, String translation, boolean isFixedExpression) {
        this.id = id;
        this.surfaceRomaji = surfaceRomaji;
        this.translation = translation;
        this.isFixedExpression = isFixedExpression;
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
}