package com.example.vocabulary_backend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

@Node("Scenario")
public class Scenario {

    @Id
    private String id;

    @Property("name")
    private String name;

    @Property("display_name")
    private String displayName;

    @Relationship(type = "HAS_STEP", direction = Relationship.Direction.OUTGOING)
    private List<SituationStep> steps;

    @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
    private List<Role> roles;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public List<SituationStep> getSteps() { return steps; }
    public List<Role> getRoles() { return roles; }
}