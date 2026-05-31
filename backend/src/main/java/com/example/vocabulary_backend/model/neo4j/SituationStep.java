package com.example.vocabulary_backend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

@Node("ScenarioStep")
public class SituationStep {

    @Id private String id;
    @Property("name") private String name;
    @Property("display_name") private String displayName;

    @Relationship(type = "USED_IN", direction = Relationship.Direction.INCOMING)
    private List<Utterance> utterances;

    @Relationship(type = "USED_IN", direction = Relationship.Direction.INCOMING)
    private List<StructuralTemplate> templates;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public List<Utterance> getUtterances() { return utterances; }
    public List<StructuralTemplate> getTemplates() { return templates; }
}