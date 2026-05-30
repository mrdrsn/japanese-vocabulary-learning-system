package com.example.vocabulary_backend.controller;

import com.example.vocabulary_backend.model.neo4j.Scenario;
import com.example.vocabulary_backend.model.neo4j.Utterance;
import com.example.vocabulary_backend.repository.ScenarioRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin  // разрешает запросы с Android-эмулятора
public class ScenarioController {

    private final ScenarioRepository scenarioRepository;

    public ScenarioController(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @GetMapping("/scenarios")
    public List<Scenario> getAllScenarios() {
        return scenarioRepository.findAll();
    }

    @GetMapping("/scenarios/{id}")
    public Scenario getScenario(@PathVariable String id) {
        return scenarioRepository.findById(id).orElseThrow();
    }

    @GetMapping("/scenarios/{id}/lexicon")
    public List<Utterance> getLexicon(@PathVariable String id) {
        return scenarioRepository.findUtterancesByScenarioId(id);
    }
}