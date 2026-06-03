package com.example.vocabulary_backend.service;

import com.example.vocabulary_backend.model.ExerciseTypeAResponse;
import com.example.vocabulary_backend.model.ExerciseTypeCResponse;
import com.example.vocabulary_backend.model.ExerciseTypeDResponse;
import com.example.vocabulary_backend.model.neo4j.*;
import com.example.vocabulary_backend.repository.ScenarioRepository;
import org.springframework.stereotype.Service;
import com.example.vocabulary_backend.model.ExerciseTypeBResponse;
import org.springframework.core.io.ClassPathResource;

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
    public List<ExerciseTypeBResponse> generateTypeBExercises(String scenarioId, int count, String roleId) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow();

        // Автоопределение роли персонала
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
        final String staffRoleId = effectiveRoleId;

        // step → utterances, utterance → stepIds, все utterances сценария
        Map<String, List<Utterance>> stepToUtterances = new LinkedHashMap<>();
        Map<String, Set<String>> utteranceToStepIds = new HashMap<>();
        Map<String, Utterance> allUtteranceMap = new LinkedHashMap<>();

        for (SituationStep step : scenario.getSteps()) {
            if (step.getUtterances() == null) continue;
            stepToUtterances.put(step.getId(), step.getUtterances());
            for (Utterance u : step.getUtterances()) {
                allUtteranceMap.put(u.getId(), u);
                utteranceToStepIds.computeIfAbsent(u.getId(), k -> new HashSet<>()).add(step.getId());
            }
        }

        // Фильтр: только роль персонала. Сначала с проверкой аудио, затем без (fallback)
        List<Utterance> candidates = filterByRoleAndAudio(allUtteranceMap, staffRoleId, true);
        if (candidates.isEmpty()) {
            candidates = filterByRoleAndAudio(allUtteranceMap, staffRoleId, false);
        }
        if (candidates.isEmpty()) return Collections.emptyList();

        Collections.shuffle(candidates);
        Random rnd = new Random();
        List<ExerciseTypeBResponse> result = new ArrayList<>();
        Set<String> usedIds = new HashSet<>();

        for (Utterance chosen : candidates) {
            if (result.size() >= count) break;
            if (usedIds.contains(chosen.getId())) continue;
            usedIds.add(chosen.getId());

            String correctTranslation = chosen.getRuTranslation() != null ? chosen.getRuTranslation() : "";
            Set<String> chosenStepIds = utteranceToStepIds.getOrDefault(
                    chosen.getId(), Collections.emptySet());

            // Варианты из того же шага (без совпадений по переводу)
            List<Utterance> sameStepPool = new ArrayList<>();
            Set<String> seenTranslations = new HashSet<>();
            seenTranslations.add(correctTranslation);
            for (String stepId : chosenStepIds) {
                for (Utterance u : stepToUtterances.getOrDefault(stepId, Collections.emptyList())) {
                    if (u.getId().equals(chosen.getId())) continue;
                    String t = u.getRuTranslation() != null ? u.getRuTranslation() : "";
                    if (seenTranslations.add(t)) sameStepPool.add(u);
                }
            }
            Collections.shuffle(sameStepPool, rnd);

            // Собираем 3 дистрактора
            List<String> distractors = new ArrayList<>();
            Set<String> usedTranslations = new HashSet<>();
            usedTranslations.add(correctTranslation);

            if (sameStepPool.size() >= 3) {
                for (int j = 0; j < 3; j++) {
                    String t = sameStepPool.get(j).getRuTranslation() != null
                            ? sameStepPool.get(j).getRuTranslation() : "";
                    distractors.add(t);
                    usedTranslations.add(t);
                }
            } else {
                for (Utterance u : sameStepPool) {
                    String t = u.getRuTranslation() != null ? u.getRuTranslation() : "";
                    distractors.add(t);
                    usedTranslations.add(t);
                }
                List<Utterance> allList = new ArrayList<>(allUtteranceMap.values());
                Collections.shuffle(allList, rnd);
                for (Utterance u : allList) {
                    if (distractors.size() >= 3) break;
                    if (u.getId().equals(chosen.getId())) continue;
                    String t = u.getRuTranslation() != null ? u.getRuTranslation() : "";
                    if (usedTranslations.add(t)) distractors.add(t);
                }
            }

            List<ExerciseTypeBResponse.OptionDto> options = new ArrayList<>();
            options.add(new ExerciseTypeBResponse.OptionDto(correctTranslation, true));
            for (String dt : distractors) {
                options.add(new ExerciseTypeBResponse.OptionDto(dt, false));
            }
            Collections.shuffle(options, rnd);

            String roleName = (chosen.getRoles() != null && !chosen.getRoles().isEmpty())
                    ? chosen.getRoles().get(0).getDisplayName() : "";
            String audioUrl = "/audio/utterances/" + chosen.getId() + ".mp3";

            result.add(new ExerciseTypeBResponse(chosen.getId(), audioUrl,
                    correctTranslation, roleName, options));
        }
        return result;
    }
    public List<ExerciseTypeCResponse> generateTypeCExercises(String scenarioId, int count) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow();

        List<Role> allRoles = scenario.getRoles() != null ? scenario.getRoles() : Collections.emptyList();

        Map<String, Utterance> allUtteranceMap = new LinkedHashMap<>();
        for (SituationStep step : scenario.getSteps()) {
            if (step.getUtterances() != null) {
                for (Utterance u : step.getUtterances()) {
                    allUtteranceMap.put(u.getId(), u);
                }
            }
        }

        List<Utterance> candidates = new ArrayList<>();
        for (Utterance u : allUtteranceMap.values()) {
            List<Role> roles = u.getRoles();
            if (roles == null || roles.size() != 1) continue;
            if (!hasAudio(u.getId())) continue;
            candidates.add(u);
        }
        if (candidates.isEmpty()) return Collections.emptyList();

        Collections.shuffle(candidates);
        Random rnd = new Random();
        List<ExerciseTypeCResponse> result = new ArrayList<>();
        Set<String> usedIds = new HashSet<>();

        for (Utterance chosen : candidates) {
            if (result.size() >= count) break;
            if (usedIds.contains(chosen.getId())) continue;
            usedIds.add(chosen.getId());

            String correctTranslation = chosen.getRuTranslation() != null ? chosen.getRuTranslation() : "";
            String correctRoleId = chosen.getRoles().get(0).getId();

            List<ExerciseTypeCResponse.RoleOptionDto> roleOptions = new ArrayList<>();
            for (Role r : allRoles) {
                roleOptions.add(new ExerciseTypeCResponse.RoleOptionDto(
                        r.getDisplayName() != null ? r.getDisplayName() : "",
                        correctRoleId.equals(r.getId())));
            }
            Collections.shuffle(roleOptions, rnd);

            List<Utterance> sameRolePool = new ArrayList<>();
            Set<String> seenTranslations = new HashSet<>();
            seenTranslations.add(correctTranslation);
            for (Utterance u : allUtteranceMap.values()) {
                if (u.getId().equals(chosen.getId())) continue;
                List<Role> uRoles = u.getRoles();
                if (uRoles == null) continue;
                boolean hasSameRole = uRoles.stream().anyMatch(r -> correctRoleId.equals(r.getId()));
                if (!hasSameRole) continue;
                String t = u.getRuTranslation() != null ? u.getRuTranslation() : "";
                if (seenTranslations.add(t)) sameRolePool.add(u);
            }
            Collections.shuffle(sameRolePool, rnd);

            List<String> distractors = new ArrayList<>();
            Set<String> usedTranslations = new HashSet<>();
            usedTranslations.add(correctTranslation);

            if (sameRolePool.size() >= 3) {
                for (int j = 0; j < 3; j++) {
                    String t = sameRolePool.get(j).getRuTranslation() != null
                            ? sameRolePool.get(j).getRuTranslation() : "";
                    distractors.add(t);
                    usedTranslations.add(t);
                }
            } else {
                for (Utterance u : sameRolePool) {
                    String t = u.getRuTranslation() != null ? u.getRuTranslation() : "";
                    distractors.add(t);
                    usedTranslations.add(t);
                }
                List<Utterance> allList = new ArrayList<>(allUtteranceMap.values());
                Collections.shuffle(allList, rnd);
                for (Utterance u : allList) {
                    if (distractors.size() >= 3) break;
                    if (u.getId().equals(chosen.getId())) continue;
                    String t = u.getRuTranslation() != null ? u.getRuTranslation() : "";
                    if (usedTranslations.add(t)) distractors.add(t);
                }
            }

            List<ExerciseTypeCResponse.TranslationOptionDto> translationOptions = new ArrayList<>();
            translationOptions.add(new ExerciseTypeCResponse.TranslationOptionDto(correctTranslation, true));
            for (String dt : distractors) {
                translationOptions.add(new ExerciseTypeCResponse.TranslationOptionDto(dt, false));
            }
            Collections.shuffle(translationOptions, rnd);

            result.add(new ExerciseTypeCResponse(
                    chosen.getId(),
                    "/audio/utterances/" + chosen.getId() + ".mp3",
                    roleOptions,
                    translationOptions));
        }
        return result;
    }
    public List<ExerciseTypeDResponse> generateTypeDExercises(String scenarioId, int count) {
        Scenario scenario = scenarioRepository.findById(scenarioId).orElseThrow();

        // Собираем все utterances сценария с привязкой к шагам
        Map<String, Utterance> allUtteranceMap = new LinkedHashMap<>();
        for (SituationStep step : scenario.getSteps()) {
            if (step.getUtterances() != null) {
                for (Utterance u : step.getUtterances()) {
                    allUtteranceMap.put(u.getId(), u);
                }
            }
        }
        String guestRoleId = null;
        if (scenario.getRoles() != null) {
            for (Role r : scenario.getRoles()) {
                if (r.getDisplayName() != null && r.getDisplayName().contains("Гость")) {
                    guestRoleId = r.getId();
                    break;
                }
            }
        }
        if (guestRoleId == null) return Collections.emptyList();
        final String finalGuestRoleId = guestRoleId;
        // Кандидаты: utterance с ролью + коммуникативным намерением + romaji
        // Хранится как пара [Utterance, SituationStep]
        List<Object[]> candidates = new ArrayList<>();
        for (SituationStep step : scenario.getSteps()) {
            if (step.getUtterances() == null) continue;
            for (Utterance u : step.getUtterances()) {
                if (u.getRoles() == null || u.getRoles().isEmpty()) continue;
                if (u.getRoles().stream().noneMatch(r -> finalGuestRoleId.equals(r.getId()))) continue;
                if (u.getCommunicativeIntent() == null) continue;
                if (u.getRomaji() == null || u.getRomaji().isEmpty()) continue;
                candidates.add(new Object[]{u, step});
            }
        }
        if (candidates.isEmpty()) return Collections.emptyList();

        Collections.shuffle(candidates);
        Random rnd = new Random();
        List<ExerciseTypeDResponse> result = new ArrayList<>();
        Set<String> usedIds = new HashSet<>();

        for (Object[] pair : candidates) {
            if (result.size() >= count) break;
            Utterance correct = (Utterance) pair[0];
            SituationStep step = (SituationStep) pair[1];

            if (usedIds.contains(correct.getId())) continue;
            usedIds.add(correct.getId());

            Role role = correct.getRoles().stream()
                    .filter(r -> finalGuestRoleId.equals(r.getId()))
                    .findFirst().orElse(correct.getRoles().get(0));
            CommunicativeIntent intent = correct.getCommunicativeIntent();
            String correctRoleId = role.getId();
            String correctIntentId = intent.getId();

            String situationYou = (role.getDisplayName() != null ? role.getDisplayName() : role.getName())
                    + " в " + (scenario.getDisplayName() != null ? scenario.getDisplayName() : scenario.getName());
            String situationStep = step.getDisplayName() != null ? step.getDisplayName() : step.getName();
            String situationIntent = intent.getDisplayName() != null ? intent.getDisplayName() : intent.getName();

            // Дистракторы: из того же шага, та же роль, другое намерение, другой romaji
            List<Utterance> distractorPool = new ArrayList<>();
            Set<String> seenRomaji = new HashSet<>();
            seenRomaji.add(correct.getRomaji());

            for (Utterance u : step.getUtterances()) {
                if (u.getId().equals(correct.getId())) continue;
                if (u.getRoles() == null || u.getRoles().isEmpty()) continue;
                if (!u.getRoles().stream().anyMatch(r -> correctRoleId.equals(r.getId()))) continue;
                if (u.getRomaji() == null || u.getRomaji().isEmpty()) continue;
                if (seenRomaji.contains(u.getRomaji())) continue;
                if (u.getCommunicativeIntent() != null && correctIntentId.equals(u.getCommunicativeIntent().getId())) continue;
                seenRomaji.add(u.getRomaji());
                distractorPool.add(u);
            }
            Collections.shuffle(distractorPool, rnd);

            // Если не хватает — добираем из всего сценария (та же роль, другое намерение)
            if (distractorPool.size() < 3) {
                List<Utterance> allList = new ArrayList<>(allUtteranceMap.values());
                Collections.shuffle(allList, rnd);
                for (Utterance u : allList) {
                    if (distractorPool.size() >= 3) break;
                    if (u.getId().equals(correct.getId())) continue;
                    if (u.getRoles() == null || u.getRoles().isEmpty()) continue;
                    if (!u.getRoles().stream().anyMatch(r -> correctRoleId.equals(r.getId()))) continue;
                    if (u.getRomaji() == null || u.getRomaji().isEmpty()) continue;
                    if (seenRomaji.contains(u.getRomaji())) continue;
                    if (u.getCommunicativeIntent() != null && correctIntentId.equals(u.getCommunicativeIntent().getId())) continue;
                    seenRomaji.add(u.getRomaji());
                    distractorPool.add(u);
                }
            }

            if (distractorPool.size() < 3) continue;

            List<ExerciseTypeDResponse.OptionDto> options = new ArrayList<>();
            options.add(new ExerciseTypeDResponse.OptionDto(correct.getRomaji(), true));
            for (int j = 0; j < 3; j++) {
                options.add(new ExerciseTypeDResponse.OptionDto(distractorPool.get(j).getRomaji(), false));
            }
            Collections.shuffle(options, rnd);

            result.add(new ExerciseTypeDResponse(situationYou, situationStep, situationIntent, options));
        }
        return result;
    }

    private List<Utterance> filterByRoleAndAudio(Map<String, Utterance> allUtteranceMap,
                                                 String staffRoleId, boolean requireAudio) {
        List<Utterance> result = new ArrayList<>();
        for (Utterance u : allUtteranceMap.values()) {
            if (staffRoleId != null) {
                List<Role> roles = u.getRoles();
                if (roles == null || roles.isEmpty()) continue;
                if (!roles.stream().allMatch(r -> staffRoleId.equals(r.getId()))) continue;
            }
            if (requireAudio && !hasAudio(u.getId())) continue;
            result.add(u);
        }
        return result;
    }

    private boolean hasAudio(String utteranceId) {
        return new ClassPathResource("static/audio/utterances/" + utteranceId + ".mp3").exists();
    }
}