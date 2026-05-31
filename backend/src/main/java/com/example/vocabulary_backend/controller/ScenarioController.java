package com.example.vocabulary_backend.controller;

import com.example.vocabulary_backend.model.ScenarioLexiconResponse;
import com.example.vocabulary_backend.model.neo4j.Scenario;
import com.example.vocabulary_backend.model.neo4j.Utterance;
import com.example.vocabulary_backend.service.ScenarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ScenarioController {

    private final ScenarioService scenarioService;

    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    @GetMapping("/scenarios")
    public List<Scenario> getAllScenarios() {
        return scenarioService.getAllScenarios();
    }

    @GetMapping("/scenarios/{id}")
    public Scenario getScenario(@PathVariable String id) {
        return scenarioService.getScenario(id);
    }

    @GetMapping("/scenarios/{id}/full-lexicon")
    public ScenarioLexiconResponse getFullLexicon(@PathVariable String id) {
        return scenarioService.getFullLexicon(id);
    }
}