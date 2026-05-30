package com.example.vocabulary_backend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Utterance")
public class Utterance {

    @Id
    private String id;

    @Property("romaji")
    private String romaji;

    @Property("ru_translation")
    private String ruTranslation;

    @Relationship(type = "SPOKEN_BY", direction = Relationship.Direction.OUTGOING)
    private Role role;

    public String getId() { return id; }
    public String getRomaji() { return romaji; }
    public String getRuTranslation() { return ruTranslation; }
    public Role getRole() { return role; }

    public String getAudioUrl() {
        return "/audio/utterances/" + id;
    }
}