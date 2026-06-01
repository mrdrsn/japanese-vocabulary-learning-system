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

        // utterances дедуплицируем по id (они не дублировались)
        // templates дедуплицируем по pattern (T1a + T1b → одна запись)
        Map<String, LexicalItemResponse> utteranceMap = new LinkedHashMap<>();
        Map<String, LexicalItemResponse> templateMap  = new LinkedHashMap<>();
        Map<String, LexicalItemResponse> luMap        = new LinkedHashMap<>();

        for (SituationStep step : scenario.getSteps()) {
            String stepName = step.getDisplayName();

            if (step.getUtterances() != null) {
                for (Utterance u : step.getUtterances()) {
                    List<String> roles = extractRoleNames(u.getRoles());
                    String intent = extractIntentName(u.getCommunicativeIntent());

                    LexicalItemResponse item = utteranceMap.computeIfAbsent(u.getId(), id -> {
                        LexicalItemResponse r = new LexicalItemResponse(
                                id, "UTTERANCE", u.getRomaji(), u.getRuTranslation());
                        r.setAudioUrl("/audio/utterances/" + id);
                        return r;
                    });
                    item.addStep(stepName);
                    item.addRoles(roles);
                    item.addIntent(intent);

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
                    List<String> roles = extractRoleNames(t.getRoles());
                    String intent = extractIntentName(t.getCommunicativeIntent());

                    // ключ — pattern, поэтому T1a и T1b сливаются в одну запись
                    LexicalItemResponse item = templateMap.computeIfAbsent(t.getPattern(),
                            k -> new LexicalItemResponse(
                                    t.getId(), "TEMPLATE", t.getPattern(), t.getTranslation()));
                    item.addStep(stepName);
                    item.addRoles(roles);
                    item.addIntent(intent);

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

        List<LexicalItemResponse> items = new ArrayList<>();
        items.addAll(utteranceMap.values());
        items.addAll(templateMap.values());
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