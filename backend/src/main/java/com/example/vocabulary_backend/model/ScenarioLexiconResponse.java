package com.example.vocabulary_backend.model;

import java.util.List;

public class ScenarioLexiconResponse {
    private List<LexicalItemResponse> items;

    public ScenarioLexiconResponse(List<LexicalItemResponse> items) {
        this.items = items;
    }

    public List<LexicalItemResponse> getItems() { return items; }
}