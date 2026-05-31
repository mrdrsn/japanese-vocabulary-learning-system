package com.example.vocabulary_backend.service;

import com.example.vocabulary_backend.model.LexicalItemResponse;
import com.example.vocabulary_backend.model.ScenarioLexiconResponse;
import com.example.vocabulary_backend.model.neo4j.*;
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

    public Scenario getScenario(String id) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow();
        filterRoles(scenario);
        return scenario;
    }

    public ScenarioLexiconResponse getFullLexicon(String scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow();
        filterRoles(scenario);

        List<LexicalItemResponse> items = new ArrayList<>();
        Set<String> seenIds = new LinkedHashSet<>();
        // LexicalUnit id → response being built (accumulates steps/roles/intents from parents)
        Map<String, LexicalItemResponse> luMap = new LinkedHashMap<>();

        for (SituationStep step : scenario.getSteps()) {
            String stepName = step.getDisplayName();

            if (step.getUtterances() != null) {
                for (Utterance u : step.getUtterances()) {
                    if (seenIds.contains(u.getId())) continue;
                    seenIds.add(u.getId());

                    List<String> roles = extractRoleNames(u.getRoles());
                    String intent = extractIntentName(u.getCommunicativeIntent());

                    LexicalItemResponse item = new LexicalItemResponse(
                            u.getId(), "UTTERANCE", u.getRomaji(), u.getRuTranslation());
                    item.addStep(stepName);
                    item.addRoles(roles);
                    item.addIntent(intent);
                    item.setAudioUrl("/audio/utterances/" + u.getId());
                    items.add(item);

                    if (u.getLexicalUnits() != null) {
                        for (LexicalUnit lu : u.getLexicalUnits()) {
                            LexicalItemResponse luItem = luMap.computeIfAbsent(lu.getId(),
                                    id -> new LexicalItemResponse(id, "LEXICAL_UNIT",
                                            lu.getRomaji(), lu.getTranslation()));
                            luItem.addStep(stepName);
                            luItem.addRoles(roles);
                            luItem.addIntent(intent);
                        }
                    }
                }
            }

            if (step.getTemplates() != null) {
                for (StructuralTemplate t : step.getTemplates()) {
                    if (seenIds.contains(t.getId())) continue;
                    seenIds.add(t.getId());

                    List<String> roles = extractRoleNames(t.getRoles());
                    String intent = extractIntentName(t.getCommunicativeIntent());

                    LexicalItemResponse item = new LexicalItemResponse(
                            t.getId(), "TEMPLATE", t.getPattern(), t.getTranslation());
                    item.addStep(stepName);
                    item.addRoles(roles);
                    item.addIntent(intent);
                    items.add(item);

                    if (t.getSlots() != null) {
                        for (Slot slot : t.getSlots()) {
                            if (slot.getLexicalUnits() == null) continue;
                            for (LexicalUnit lu : slot.getLexicalUnits()) {
                                LexicalItemResponse luItem = luMap.computeIfAbsent(lu.getId(),
                                        id -> new LexicalItemResponse(id, "LEXICAL_UNIT",
                                                lu.getRomaji(), lu.getTranslation()));
                                luItem.addStep(stepName);
                                luItem.addRoles(roles);
                                luItem.addIntent(intent);
                            }
                        }
                    }
                }
            }
        }

        items.addAll(luMap.values());
        return new ScenarioLexiconResponse(items);
    }

    private List<String> extractRoleNames(List<Role> roles) {
        if (roles == null) return Collections.emptyList();
        return roles.stream()
                .map(Role::getDisplayName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String extractIntentName(CommunicativeIntent intent) {
        return intent != null ? intent.getDisplayName() : null;
    }

    private void filterRoles(Scenario scenario) {
        if (scenario.getRoles() == null || scenario.getRoles().isEmpty()) return;

        Set<String> validRoleIds = scenario.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        for (SituationStep step : scenario.getSteps()) {
            if (step.getUtterances() != null) {
                for (Utterance u : step.getUtterances()) {
                    if (u.getRoles() != null) {
                        u.setRoles(u.getRoles().stream()
                                .filter(r -> validRoleIds.contains(r.getId()))
                                .collect(Collectors.toList()));
                    }
                }
            }
            if (step.getTemplates() != null) {
                for (StructuralTemplate t : step.getTemplates()) {
                    if (t.getRoles() != null) {
                        t.setRoles(t.getRoles().stream()
                                .filter(r -> validRoleIds.contains(r.getId()))
                                .collect(Collectors.toList()));
                    }
                }
            }
        }
    }
}