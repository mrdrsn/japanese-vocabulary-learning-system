package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.japanesevocabularylearningsystem.fragment.ExerciseTypeAFragment;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeA;
import com.example.japanesevocabularylearningsystem.network.ApiClient;
import com.example.japanesevocabularylearningsystem.network.dto.ExerciseTypeADto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingActivity extends AppCompatActivity {

    private static final int TYPE_A_COUNT = 2;

    private String scenarioId;
    private final List<ExerciseTypeA> exercises = new ArrayList<>();
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        scenarioId = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_ID);
        if (scenarioId == null) scenarioId = "SC1";
        if (savedInstanceState == null) loadExercises();
    }

    private void loadExercises() {
        ApiClient.getInstance()
                .getTypeAExercises(scenarioId, TYPE_A_COUNT, "auto")
                .enqueue(new Callback<List<ExerciseTypeADto>>() {
                    @Override
                    public void onResponse(Call<List<ExerciseTypeADto>> call,
                                           Response<List<ExerciseTypeADto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (ExerciseTypeADto dto : response.body()) {
                                exercises.add(dto.toModel());
                            }
                        }
                        if (exercises.isEmpty()) {
                            Toast.makeText(TrainingActivity.this,
                                    "Нет упражнений", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            showCurrentExercise();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ExerciseTypeADto>> call, Throwable t) {
                        Toast.makeText(TrainingActivity.this,
                                "Ошибка загрузки: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void showCurrentExercise() {
        if (currentIndex >= exercises.size()) {
            Intent intent = new Intent(this, TrainingFinishedActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_ID, scenarioId);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_NAME,
                    getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_NAME));
            startActivity(intent);
            finish();
            return;
        }

        ExerciseTypeAFragment f = ExerciseTypeAFragment.newInstance(
                exercises.get(currentIndex), currentIndex, exercises.size());
        f.setOnAnswerSubmittedListener(this::onAnswerSubmitted);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.exerciseContainer, f);
        tx.commit();
    }

    private void onAnswerSubmitted() {
        currentIndex++;
        showCurrentExercise();
    }
}