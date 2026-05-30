package com.example.vocabulary_backend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("LexicalUnit")
public class LexicalUnit {
    @Id private String id;
    @Property("romaji") private String romaji;
    @Property("translation") private String translation;

    public String getId() { return id; }
    public String getRomaji() { return romaji; }
    public String getTranslation() { return translation; }
}