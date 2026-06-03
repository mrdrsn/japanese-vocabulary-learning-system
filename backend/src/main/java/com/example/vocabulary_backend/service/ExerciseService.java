package com.example.vocabulary_backend.service;

import com.example.vocabulary_backend.model.ExerciseTypeAResponse;
import com.example.vocabulary_backend.model.neo4j.*;
import com.example.vocabulary_backend.repository.ScenarioRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    private final ScenarioRepository scenarioRepository;

    public ExerciseService(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    public List<ExerciseTypeAResponse> generateTypeAExercises(String scenarioId, int count, String roleId) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow();

        // Автоопределение роли персонала: первая роль, чьё display_name не содержит "Гость"
        String effectiveRoleId;
        if ("auto".equals(roleId)) {
            effectiveRoleId = null;
            if (scenario.getRoles() != null) {
                for (Role r : scenario.getRoles()) {
                    if (r.getDisplayName() != null && !r.getDisplayName().contains("Гость")) {
                        effectiveRoleId = r.getId();
                        break;
                    }
                }
            }
        } else {
            effectiveRoleId = roleId;
        }
        final String finalRoleId = effectiveRoleId;
        List<StructuralTemplate> candidates = new ArrayList<>();
        Map<String, LexicalUnit> allLuMap = new LinkedHashMap<>();

        for (SituationStep step : scenario.getSteps()) {
            if (step.getTemplates() != null) {
                for (StructuralTemplate t : step.getTemplates()) {
                    if (t.getSlots() != null) {
                        for (TemplateSlotRelation rel : t.getSlots()) {
                            if (rel.getSlot() != null && rel.getSlot().getLexicalUnits() != null) {
                                for (LexicalUnit lu : rel.getSlot().getLexicalUnits()) {
                                    allLuMap.put(lu.getId(), lu);
                                }
                            }
                        }
                    }
                    List<Role> roles = t.getRoles();
                    boolean roleMatch = finalRoleId == null
                            ? (roles != null && !roles.isEmpty())
                            : (roles != null && !roles.isEmpty()
                               && roles.stream().allMatch(r -> finalRoleId.equals(r.getId())));
                    if (roleMatch) {
                        candidates.add(t);
                    }
                }
            }
            if (step.getUtterances() != null) {
                for (Utterance u : step.getUtterances()) {
                    if (u.getLexicalUnits() != null) {
                        for (LexicalUnit lu : u.getLexicalUnits()) {
                            allLuMap.put(lu.getId(), lu);
                        }
                    }
                }
            }
        }

        if (candidates.isEmpty()) return Collections.emptyList();

        Collections.shuffle(candidates);
        Random rnd = new Random();
        List<ExerciseTypeAResponse> result = new ArrayList<>();

        for (int i = 0; i < count && i < candidates.size(); i++) {
            StructuralTemplate t = candidates.get(i);

            List<LexicalUnit> slotLus = firstSlotLus(t);
            if (slotLus.isEmpty()) continue;
// Выбираем одну случайную ЛЕ из слота как правильный ответ
            LexicalUnit answerLu = slotLus.get(rnd.nextInt(slotLus.size()));
            List<LexicalUnit> correctLus = Collections.singletonList(answerLu);
            Set<String> correctIds = Collections.singleton(answerLu.getId());

            List<LexicalUnit> distractors = allLuMap.values().stream()
                    .filter(lu -> !correctIds.contains(lu.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(distractors, rnd);

            List<ExerciseTypeAResponse.OptionDto> options = new ArrayList<>();
            for (LexicalUnit lu : correctLus) {
                options.add(new ExerciseTypeAResponse.OptionDto(
                        lu.getId(), lu.getRomaji() != null ? lu.getRomaji() : "", true));
            }
            int needed = 4 - options.size();
            for (int j = 0; j < needed && j < distractors.size(); j++) {
                LexicalUnit lu = distractors.get(j);
                options.add(new ExerciseTypeAResponse.OptionDto(
                        lu.getId(), lu.getRomaji() != null ? lu.getRomaji() : "", false));
            }
            Collections.shuffle(options, rnd);

            String roleName = t.getRoles().stream()
                    .findFirst().map(Role::getDisplayName).orElse("");
            String pattern = t.getPattern() != null ? t.getPattern() : "";
            String romajiWithGap = pattern.replace("[X]", "_____").replace("[Y]", "_____");

            String translation = t.getTranslation() != null ? t.getTranslation() : "";
            String filledTranslation = translation;
            if (!correctLus.isEmpty()) {
                String luT = correctLus.get(0).getTranslation() != null
                        ? correctLus.get(0).getTranslation() : "_____";
                filledTranslation = filledTranslation.replace("[X]", luT).replace("[Y]", "_____");
            }
            result.add(new ExerciseTypeAResponse(
                    romajiWithGap, filledTranslation, roleName, options));
        }

        return result;
    }

    private List<LexicalUnit> firstSlotLus(StructuralTemplate t) {
        if (t.getSlots() == null || t.getSlots().isEmpty()) return Collections.emptyList();
        TemplateSlotRelation first = t.getSlots().stream()
                .filter(r -> r.getSlot() != null)
                .min(Comparator.comparingInt(r -> r.getPosition() != null ? r.getPosition() : 0))
                .orElse(null);
        if (first == null || first.getSlot().getLexicalUnits() == null) return Collections.emptyList();
        return first.getSlot().getLexicalUnits();
    }
}