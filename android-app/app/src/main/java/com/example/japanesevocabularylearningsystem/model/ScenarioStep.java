package com.example.japanesevocabularylearningsystem.model;

import java.io.Serializable;

public class ScenarioStep implements Serializable {
    private String id;
    private String name;
    private boolean isSelected;

    public ScenarioStep(String id, String name, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}