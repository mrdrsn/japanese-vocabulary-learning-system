package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.fragment.ExerciseTypeAFragment;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeA;

import java.util.List;

public class TrainingActivity extends AppCompatActivity {

    private List<ExerciseTypeA> exercises;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        exercises = MockDataProvider.getTypeAExercises();

        if (savedInstanceState == null) {
            showCurrentExercise();
        }
    }

    private void showCurrentExercise() {
        ExerciseTypeA exercise = exercises.get(currentIndex);

        ExerciseTypeAFragment fragment = ExerciseTypeAFragment.newInstance(
                exercise, currentIndex, exercises.size());
        fragment.setOnAnswerSubmittedListener(this::onAnswerSubmitted);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.exerciseContainer, fragment);
        tx.commit();
    }

    private void onAnswerSubmitted() {
        if (currentIndex < exercises.size() - 1) {
            currentIndex++;
            showCurrentExercise();
        } else {
            Intent intent = new Intent(this, TrainingFinishedActivity.class);
            startActivity(intent);
            finish();
        }
    }
}