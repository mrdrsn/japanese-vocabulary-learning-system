package com.example.vocabulary_backend.model;

import com.example.vocabulary_backend.model.neo4j.LexicalUnit;
import com.example.vocabulary_backend.model.neo4j.StructuralTemplate;
import com.example.vocabulary_backend.model.neo4j.Utterance;

import java.util.List;

public class ScenarioLexiconResponse {
    private List<Utterance> utterances;
    private List<StructuralTemplate> structuralTemplates;
    private List<LexicalUnit> lexicalUnits;

    public ScenarioLexiconResponse(List<Utterance> utterances,
                                   List<StructuralTemplate> structuralTemplates,
                                   List<LexicalUnit> lexicalUnits) {
        this.utterances = utterances;
        this.structuralTemplates = structuralTemplates;
        this.lexicalUnits = lexicalUnits;
    }

    public List<Utterance> getUtterances() { return utterances; }
    public List<StructuralTemplate> getStructuralTemplates() { return structuralTemplates; }
    public List<LexicalUnit> getLexicalUnits() { return lexicalUnits; }
}