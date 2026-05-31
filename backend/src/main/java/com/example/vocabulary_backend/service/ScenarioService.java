package com.example.vocabulary_backend.service;

import com.example.vocabulary_backend.model.ScenarioLexiconResponse;
import com.example.vocabulary_backend.model.neo4j.LexicalUnit;
import com.example.vocabulary_backend.model.neo4j.Scenario;
import com.example.vocabulary_backend.model.neo4j.StructuralTemplate;
import com.example.vocabulary_backend.model.neo4j.Utterance;
import com.example.vocabulary_backend.repository.ScenarioRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;

    public ScenarioService(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    public List<Scenario> getAllScenarios() {
        return scenarioRepository.findAll();
    }

    public Scenario getScenario(String scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow();
        filterRoles(scenario);
        return scenario;
    }
    

    public ScenarioLexiconResponse getFullLexicon(String scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow();
        filterRoles(scenario);

        List<Utterance> utterances = scenario.getSteps().stream()
                .filter(s -> s.getUtterances() != null)
                .flatMap(s -> s.getUtterances().stream())
                .distinct()
                .collect(Collectors.toList());

        List<StructuralTemplate> templates = scenario.getSteps().stream()
                .filter(s -> s.getTemplates() != null)
                .flatMap(s -> s.getTemplates().stream())
                .distinct()
                .collect(Collectors.toList());

        Map<String, LexicalUnit> luMap = new LinkedHashMap<>();
        utterances.stream()
                .filter(u -> u.getLexicalUnits() != null)
                .flatMap(u -> u.getLexicalUnits().stream())
                .forEach(lu -> luMap.put(lu.getId(), lu));
        templates.stream()
                .filter(t -> t.getSlots() != null)
                .flatMap(t -> t.getSlots().stream())
                .filter(s -> s.getLexicalUnits() != null)
                .flatMap(s -> s.getLexicalUnits().stream())
                .forEach(lu -> luMap.put(lu.getId(), lu));

        return new ScenarioLexiconResponse(utterances, templates, new ArrayList<>(luMap.values()));
    }

    private void filterRoles(Scenario scenario) {
        if (scenario.getRoles() == null || scenario.getSteps() == null) return;

        Set<String> validRoleIds = scenario.getRoles().stream()
                .map(r -> r.getId())
                .collect(Collectors.toSet());

        scenario.getSteps().forEach(step -> {
            if (step.getUtterances() != null) {
                step.getUtterances().forEach(u -> {
                    if (u.getRoles() != null) {
                        u.setRoles(u.getRoles().stream()
                                .filter(r -> validRoleIds.contains(r.getId()))
                                .collect(Collectors.toList()));
                    }
                });
            }
            if (step.getTemplates() != null) {
                step.getTemplates().forEach(t -> {
                    if (t.getRoles() != null) {
                        t.setRoles(t.getRoles().stream()
                                .filter(r -> validRoleIds.contains(r.getId()))
                                .collect(Collectors.toList()));
                    }
                });
            }
        });
    }
}