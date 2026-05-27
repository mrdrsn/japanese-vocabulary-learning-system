package com.example.japanesevocabularylearningsystem.model;

public class CommunicativeIntent {

    private String id;
    private String name;

    public CommunicativeIntent(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}