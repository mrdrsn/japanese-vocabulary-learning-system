package com.example.vocabulary_backend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("CommunicativeIntent")
public class CommunicativeIntent {
    @Id private String id;
    @Property("name") private String name;
    @Property("display_name") private String displayName;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
}