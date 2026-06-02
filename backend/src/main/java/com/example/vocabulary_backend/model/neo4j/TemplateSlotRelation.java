package com.example.vocabulary_backend.model.neo4j;

import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class TemplateSlotRelation {

    @RelationshipId
    private Long id;

    @Property("position")
    private Integer position;

    @TargetNode
    private Slot slot;

    public Long getId() { return id; }
    public Integer getPosition() { return position; }
    public Slot getSlot() { return slot; }
}