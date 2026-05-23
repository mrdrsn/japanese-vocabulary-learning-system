package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainingActivity extends AppCompatActivity {

    private TextView tvExerciseCounter;
    private View indicator1;
    private View indicator2;
    private TextView tvInstruction;
    private TextView tvTranslation;
    private TextView tvRomajiGap;
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnSubmitAnswer;

    private int currentExerciseIndex = 0;
    private Button selectedButton = null;

    private List<TrainingItem> exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        tvExerciseCounter = findViewById(R.id.tvExerciseCounter);
        indicator1 = findViewById(R.id.indicator1);
        indicator2 = findViewById(R.id.indicator2);
        tvInstruction = findViewById(R.id.tvInstruction);
        tvTranslation = findViewById(R.id.tvTranslation);
        tvRomajiGap = findViewById(R.id.tvRomajiGap);

        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnSubmitAnswer = findViewById(R.id.btnSubmitAnswer);

        exercises = createMockExercises();

        setupOptionClick(btnOption1);
        setupOptionClick(btnOption2);
        setupOptionClick(btnOption3);
        setupOptionClick(btnOption4);

        btnSubmitAnswer.setOnClickListener(v -> {
            if (currentExerciseIndex < exercises.size() - 1) {
                currentExerciseIndex++;
                showExercise(currentExerciseIndex);
            } else {
                Intent intent = new Intent(TrainingActivity.this, TrainingFinishedActivity.class);
                startActivity(intent);
                finish();
            }
        });

        showExercise(currentExerciseIndex);
    }

    private void setupOptionClick(Button button) {
        button.setOnClickListener(v -> {
            resetOptionStyles();

            selectedButton = button;
            selectedButton.setBackgroundResource(R.drawable.bg_option_selected);
            selectedButton.setTextColor(getResources().getColor(android.R.color.white));

            btnSubmitAnswer.setVisibility(View.VISIBLE);
        });
    }

    private void resetOptionStyles() {
        Button[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};

        for (Button button : buttons) {
            button.setBackgroundResource(R.drawable.bg_option_default);
            button.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void showExercise(int index) {
        TrainingItem item = exercises.get(index);

        tvExerciseCounter.setText((index + 1) + "/" + exercises.size());
        tvInstruction.setText(item.instruction);
        tvTranslation.setText(item.translation);
        tvRomajiGap.setText(item.romajiWithGap);

        btnOption1.setText(item.options.get(0));
        btnOption2.setText(item.options.get(1));
        btnOption3.setText(item.options.get(2));
        btnOption4.setText(item.options.get(3));

        resetOptionStyles();
        selectedButton = null;
        btnSubmitAnswer.setVisibility(View.GONE);

        if (index == 0) {
            indicator1.setBackgroundResource(R.drawable.bg_indicator_red);
            indicator2.setBackgroundResource(R.drawable.bg_indicator_grey);
        } else {
            indicator1.setBackgroundResource(R.drawable.bg_indicator_red);
            indicator2.setBackgroundResource(R.drawable.bg_indicator_red);
        }
    }

    private List<TrainingItem> createMockExercises() {
        List<TrainingItem> list = new ArrayList<>();

        list.add(new TrainingItem(
                "Закончите фразу продавца...",
                "Вам разогреть это?",
                "Kochira ni ________masuka?",
                Arrays.asList("goiriyō", "atatame", "okute", "ijō")
        ));

        list.add(new TrainingItem(
                "Закончите фразу продавца...",
                "Вам нужен пакет?",
                "Rejibukuro wa ________ desu ka?",
                Arrays.asList("goiriyō", "atatame", "okute", "masuka")
        ));

        return list;
    }

    private static class TrainingItem {
        String instruction;
        String translation;
        String romajiWithGap;
        List<String> options;

        TrainingItem(String instruction, String translation, String romajiWithGap, List<String> options) {
            this.instruction = instruction;
            this.translation = translation;
            this.romajiWithGap = romajiWithGap;
            this.options = options;
        }
    }
}