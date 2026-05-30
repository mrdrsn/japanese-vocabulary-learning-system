package com.example.vocabulary_backend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

@Node("Slot")
public class Slot {
    @Id private String id;
    @Property("name") private String name;

    @Relationship(type = "CAN_BE_FILLED_BY", direction = Relationship.Direction.OUTGOING)
    private List<LexicalUnit> lexicalUnits;

    public String getId() { return id; }
    public String getName() { return name; }
    public List<LexicalUnit> getLexicalUnits() { return lexicalUnits; }
}