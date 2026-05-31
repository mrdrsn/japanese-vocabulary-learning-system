package com.example.vocabulary_backend.repository;

import com.example.vocabulary_backend.model.neo4j.Scenario;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScenarioRepository extends Neo4jRepository<Scenario, String> {
}