package com.example.vocabulary_backend.service;

import com.example.vocabulary_backend.model.neo4j.Scenario;
import com.example.vocabulary_backend.model.neo4j.Utterance;
import com.example.vocabulary_backend.repository.ScenarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
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

    public List<Utterance> getUtterances(String scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow();
        filterRoles(scenario);
        return scenario.getSteps().stream()
                .filter(step -> step.getUtterances() != null)
                .flatMap(step -> step.getUtterances().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private void filterRoles(Scenario scenario) {
        if (scenario.getRoles() == null || scenario.getSteps() == null) return;

        Set<String> validRoleIds = scenario.getRoles().stream()
                .map(r -> r.getId())
                .collect(Collectors.toSet());

        scenario.getSteps().forEach(step -> {
            if (step.getUtterances() == null) return;
            step.getUtterances().forEach(utterance -> {
                if (utterance.getRoles() != null) {
                    utterance.setRoles(
                            utterance.getRoles().stream()
                                    .filter(r -> validRoleIds.contains(r.getId()))
                                    .collect(Collectors.toList())
                    );
                }
            });
        });
    }
}