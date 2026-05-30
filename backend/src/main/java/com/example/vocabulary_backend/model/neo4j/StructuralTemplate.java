package com.example.vocabulary_backend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

@Node("StructuralTemplate")
public class StructuralTemplate {
    @Id private String id;
    @Property("pattern") private String pattern;
    @Property("translation") private String translation;

    @Relationship(type = "SPOKEN_BY", direction = Relationship.Direction.OUTGOING)
    private Role role;

    @Relationship(type = "EXPRESSES", direction = Relationship.Direction.OUTGOING)
    private CommunicativeIntent communicativeIntent;

    @Relationship(type = "HAS_SLOT", direction = Relationship.Direction.OUTGOING)
    private List<Slot> slots;

    public String getId() { return id; }
    public String getPattern() { return pattern; }
    public String getTranslation() { return translation; }
    public Role getRole() { return role; }
    public CommunicativeIntent getCommunicativeIntent() { return communicativeIntent; }
    public List<Slot> getSlots() { return slots; }
}