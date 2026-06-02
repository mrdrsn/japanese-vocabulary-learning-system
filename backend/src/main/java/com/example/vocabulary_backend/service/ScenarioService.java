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

                    LexicalItemResponse item = templateMap.computeIfAbsent(t.getPattern(),
                            k -> new LexicalItemResponse(t.getId(), "TEMPLATE",
                                    t.getPattern(), t.getTranslation()));
                    item.addStep(stepName);
                    item.addRoles(roles);
                    item.addIntent(intent);

                    if (t.getSlots() != null && !t.getSlots().isEmpty()) {
                        // Сортируем слоты по позиции связи
                        List<TemplateSlotRelation> sortedSlots = t.getSlots().stream()
                                .filter(r -> r.getSlot() != null)
                                .sorted(Comparator.comparingInt(
                                        r -> r.getPosition() != null ? r.getPosition() : 0))
                                .collect(Collectors.toList());

                        // Все ЛЕ из всех слотов → в luMap
                        for (TemplateSlotRelation relation : sortedSlots) {
                            Slot slot = relation.getSlot();
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

                        String pattern = t.getPattern() != null ? t.getPattern() : "";
                        String translation = t.getTranslation() != null ? t.getTranslation() : "";

                        if (sortedSlots.size() == 1) {
                            // Один слот: заменяем [X] или ________
                            Slot slot = sortedSlots.get(0).getSlot();
                            if (slot.getLexicalUnits() != null) {
                                for (LexicalUnit lu : slot.getLexicalUnits()) {
                                    String luR = lu.getRomaji() != null ? lu.getRomaji() : "";
                                    String luT = lu.getTranslation() != null ? lu.getTranslation() : "";
                                    item.addExample(new LexicalItemResponse.ExampleEntry(
                                            lu.getId(), luR, fillSlot(pattern, luR), fillSlot(translation, luT)));
                                }
                            }
                        } else {
                            // Два слота: перебираем все комбинации [X] × [Y]
                            Slot slot1 = sortedSlots.get(0).getSlot();
                            Slot slot2 = sortedSlots.get(1).getSlot();
                            List<LexicalUnit> lus1 = slot1.getLexicalUnits() != null
                                    ? slot1.getLexicalUnits() : Collections.emptyList();
                            List<LexicalUnit> lus2 = slot2.getLexicalUnits() != null
                                    ? slot2.getLexicalUnits() : Collections.emptyList();

                            for (LexicalUnit lu1 : lus1) {
                                for (LexicalUnit lu2 : lus2) {
                                    String lu1R = lu1.getRomaji() != null ? lu1.getRomaji() : "";
                                    String lu2R = lu2.getRomaji() != null ? lu2.getRomaji() : "";
                                    String lu1T = lu1.getTranslation() != null ? lu1.getTranslation() : "";
                                    String lu2T = lu2.getTranslation() != null ? lu2.getTranslation() : "";
                                    String filledR = pattern.replace("[X]", lu1R).replace("[Y]", lu2R);
                                    String filledT = translation.replace("[X]", lu1T).replace("[Y]", lu2T);
                                    // lu1 (позиция 1) — основная ЛЕ для подсветки и навигации
                                    LexicalItemResponse.ExampleEntry entry2 =
                                            new LexicalItemResponse.ExampleEntry(lu1.getId(), lu1R, filledR, filledT);
                                    entry2.setLuId2(lu2.getId());
                                    entry2.setLuRomaji2(lu2R);
                                    item.addExample(entry2);
                                }
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
    private String fillSlot(String template, String filler) {
        if (template.contains("[X]")) return template.replace("[X]", filler);
        if (template.contains("________")) return template.replace("________", filler);
        return template;
    }
}