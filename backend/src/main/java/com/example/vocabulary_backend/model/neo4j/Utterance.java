package com.example.vocabulary_backend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

@Node("Utterance")
public class Utterance {

    @Id private String id;
    @Property("romaji") private String romaji;
    @Property("ru_translation") private String ruTranslation;

    @Relationship(type = "SPOKEN_BY", direction = Relationship.Direction.OUTGOING)
    private List<Role> roles;

    @Relationship(type = "CONSISTS_OF", direction = Relationship.Direction.OUTGOING)
    private List<LexicalUnit> lexicalUnits;

    @Relationship(type = "EXPRESSES", direction = Relationship.Direction.OUTGOING)
    private CommunicativeIntent communicativeIntent;

    public String getId() { return id; }
    public String getRomaji() { return romaji; }
    public String getRuTranslation() { return ruTranslation; }
    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }
    public List<LexicalUnit> getLexicalUnits() { return lexicalUnits; }
    public CommunicativeIntent getCommunicativeIntent() { return communicativeIntent; }
    public String getAudioUrl() { return "/audio/utterances/" + id + ".mp3"; }
}