package com.example.vocabulary_backend.repository;

import com.example.vocabulary_backend.model.neo4j.Scenario;
import com.example.vocabulary_backend.model.neo4j.Utterance;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScenarioRepository extends Neo4jRepository<Scenario, String> {

    @Query("""
        MATCH (sc:Scenario {id: $scenarioId})-[:HAS_STEP]->(step:SituationStep)
        MATCH (u:Utterance)-[:USED_IN]->(step)
        MATCH (u)-[:SPOKEN_BY]->(r:Role)
        OPTIONAL MATCH (u)-[:EXPRESSES]->(ci:CommunicativeIntent)
        RETURN u, r, ci, step
    """)
    List<Utterance> findUtterancesByScenarioId(String scenarioId);
}