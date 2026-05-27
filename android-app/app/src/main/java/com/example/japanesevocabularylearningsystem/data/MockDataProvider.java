package com.example.japanesevocabularylearningsystem.data;

import com.example.japanesevocabularylearningsystem.model.AnswerOption;
import com.example.japanesevocabularylearningsystem.model.CommunicativeIntent;
import com.example.japanesevocabularylearningsystem.model.Exercise;
import com.example.japanesevocabularylearningsystem.model.Role;
import com.example.japanesevocabularylearningsystem.model.Scenario;
import com.example.japanesevocabularylearningsystem.model.ScenarioStep;
import com.example.japanesevocabularylearningsystem.model.Utterance;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockDataProvider {

    public static List<Scenario> getScenarios() {
        List<Scenario> scenarios = new ArrayList<>();

        scenarios.add(new Scenario("SC1", "Convenience Store"));
        scenarios.add(new Scenario("SC2", "Cafe / Restaurant"));

        return scenarios;
    }


    public static List<Role> getRoles() {
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("R1", "Гость"));
        roles.add(new Role("R2", "Кассир"));
        return roles;
    }

    public static Role getRoleById(String id) {
        for (Role role : getRoles()) {
            if (role.getId().equals(id)) return role;
        }
        return null;
    }

    public static List<CommunicativeIntent> getCommunicativeIntents() {
        List<CommunicativeIntent> intents = new ArrayList<>();
        intents.add(new CommunicativeIntent("CI1", "Вопрос"));
        intents.add(new CommunicativeIntent("CI2", "Предложение"));
        intents.add(new CommunicativeIntent("CI3", "Согласие"));
        intents.add(new CommunicativeIntent("CI4", "Отказ"));
        intents.add(new CommunicativeIntent("CI5", "Просьба"));
        intents.add(new CommunicativeIntent("CI6", "Приветствие"));
        intents.add(new CommunicativeIntent("CI7", "Благодарность"));
        intents.add(new CommunicativeIntent("CI8", "Уточнение"));
        return intents;
    }

    public static CommunicativeIntent getIntentById(String id) {
        for (CommunicativeIntent intent : getCommunicativeIntents()) {
            if (intent.getId().equals(id)) return intent;
        }
        return null;
    }


    public static List<ExerciseTypeA> getTypeAExercises() {
        List<ExerciseTypeA> list = new ArrayList<>();

        list.add(new ExerciseTypeA(
                "Закончите фразу продавца...",
                "Вам разогреть это?",
                "Kochira ni ________masuka?",
                Arrays.asList("goiriyō", "atatame", "okute", "ijō")
        ));

        list.add(new ExerciseTypeA(
                "Закончите фразу продавца...",
                "Вам нужен пакет?",
                "Rejibukuro wa ________ desu ka?",
                Arrays.asList("goiriyō", "atatame", "okute", "masuka")
        ));

        return list;
    }
    // Шаги сценария "Convenience Store"
    public static List<ScenarioStep> getConvenienceStoreSteps() {
        List<ScenarioStep> steps = new ArrayList<>();
        steps.add(new ScenarioStep("S1", "Приветствие", true));
        steps.add(new ScenarioStep("S2", "Разогрев еды", true));
        steps.add(new ScenarioStep("S3", "Дополнительные приборы", true));
        steps.add(new ScenarioStep("S4", "Вопрос о пакете на кассе", true));
        steps.add(new ScenarioStep("S5", "Вопрос о бонусной карте", true));
        steps.add(new ScenarioStep("S6", "Оплата", true));
        steps.add(new ScenarioStep("S7", "Уход гостя", true));
        return steps;
    }

    // Какие utterance ID относятся к каждому шагу (временная заглушка — до подключения БД)
    public static Map<String, List<String>> getConvenienceStoreStepMap() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("S1", Arrays.asList("U9"));
        map.put("S2", Arrays.asList("U6", "U7", "U8"));
        map.put("S3", Arrays.asList("U3", "U5"));
        map.put("S4", Arrays.asList("U1", "U2", "U4"));
        map.put("S5", Arrays.asList());
        map.put("S6", Arrays.asList());
        map.put("S7", Arrays.asList("U9"));
        return map;
    }
    public static List<Utterance> getConvenienceStoreUtterances() {
        List<Utterance> utterances = new ArrayList<>();

        utterances.add(new Utterance("U1",
                "Rejibukuro wa irimasu ka",
                "Вам нужен пакет?",
                false, "R2", "CI1", "Оплата"));

        utterances.add(new Utterance("U2",
                "Rejibukuro goiriyou desu ka",
                "Вам нужен пакет?",
                false, "R2", "CI2", "Оплата"));

        utterances.add(new Utterance("U3",
                "Supuun wa yoroshii desu ka",
                "Вам нужна ложка?",
                false, "R2", "CI1", "Завершение"));

        utterances.add(new Utterance("U4",
                "Rejibukuro wa daijoubu desu",
                "Пакет не нужен.",
                false, "R1", "CI4", "Оплата"));

        utterances.add(new Utterance("U5",
                "Ohashi onegaishimasu",
                "Дайте, пожалуйста, палочки для еды.",
                false, "R1", "CI5", "Получение товара"));

        utterances.add(new Utterance("U6",
                "Kochira atatamemasu ka",
                "Вам разогреть это?",
                true, "R2", "CI1", "Разогрев"));

        utterances.add(new Utterance("U7",
                "Hai onegaishimasu",
                "Да, пожалуйста.",
                true, "R1", "CI3", "Разогрев"));

        utterances.add(new Utterance("U8",
                "Daijoubu desu",
                "Нет, спасибо.",
                true, "R1", "CI4", "Оплата"));

        utterances.add(new Utterance("U9",
                "Arigato gozaimasu",
                "Большое спасибо.",
                true, "R2", "CI7", "Завершение"));

        return utterances;
    }

    public static Utterance getUtteranceById(String id) {
        for (Utterance u : getConvenienceStoreUtterances()) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }
    public static List<Exercise> getConvenienceStoreExercises() {
        List<Exercise> exercises = new ArrayList<>();

        exercises.add(new Exercise(
                "Rejibukuro wa irimasu ka",
                Arrays.asList(
                        new AnswerOption("Вам нужен пакет?", true),
                        new AnswerOption("Где касса?", false),
                        new AnswerOption("Спасибо большое.", false)
                )
        ));

        exercises.add(new Exercise(
                "Ohashi onegaishimasu",
                Arrays.asList(
                        new AnswerOption("Пожалуйста, разогрейте это.", false),
                        new AnswerOption("Дайте, пожалуйста, палочки для еды.", true),
                        new AnswerOption("Нет, спасибо.", false)
                )
        ));

        exercises.add(new Exercise(
                "Kochira atatamemasu ka",
                Arrays.asList(
                        new AnswerOption("Вам разогреть это?", true),
                        new AnswerOption("Вам нужен пакет?", false),
                        new AnswerOption("Я расплачусь картой.", false)
                )
        ));

        return exercises;
    }
}

