package com.example.japanesevocabularylearningsystem.data;

import com.example.japanesevocabularylearningsystem.model.AnswerOption;
import com.example.japanesevocabularylearningsystem.model.Exercise;
import com.example.japanesevocabularylearningsystem.model.Scenario;
import com.example.japanesevocabularylearningsystem.model.Utterance;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockDataProvider {

    public static List<Scenario> getScenarios() {
        List<Scenario> scenarios = new ArrayList<>();

        scenarios.add(new Scenario("SC1", "Convenience Store"));
        scenarios.add(new Scenario("SC2", "Cafe / Restaurant"));

        return scenarios;
    }

    public static List<Utterance> getConvenienceStoreUtterances() {
        List<Utterance> utterances = new ArrayList<>();

        utterances.add(new Utterance("U1", "Rejibukuro wa irimasu ka", "Вам нужен пакет?", false));
        utterances.add(new Utterance("U2", "Rejibukuro goiriyou desu ka", "Вам нужен пакет?", false));
        utterances.add(new Utterance("U3", "Supuun wa yoroshii desu ka", "Вам нужна ложка?", false));
        utterances.add(new Utterance("U4", "Rejibukuro wa daijoubu desu", "Пакет не нужен.", false));
        utterances.add(new Utterance("U5", "Ohashi onegaishimasu", "Дайте, пожалуйста, палочки для еды.", false));
        utterances.add(new Utterance("U6", "Kochira atatamemasu ka", "Вам разогреть это?", true));
        utterances.add(new Utterance("U7", "Hai onegaishimasu", "Да, пожалуйста.", true));
        utterances.add(new Utterance("U8", "Daijoubu desu", "Нет, спасибо.", true));
        utterances.add(new Utterance("U9", "Arigato gozaimasu", "Большое спасибо.", true));

        return utterances;
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
}