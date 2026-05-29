package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.fragment.ExerciseTypeAFragment;
import com.example.japanesevocabularylearningsystem.fragment.ExerciseTypeBFragment;
import com.example.japanesevocabularylearningsystem.fragment.ExerciseTypeCFragment;
import com.example.japanesevocabularylearningsystem.fragment.ExerciseTypeDFragment;
import com.example.japanesevocabularylearningsystem.model.TrainingExercise;

import java.util.List;

public class TrainingActivity extends AppCompatActivity {

    private List<TrainingExercise> exercises;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        exercises = MockDataProvider.getTrainingExercises();
        if (savedInstanceState == null) showCurrentExercise();
    }

    private void showCurrentExercise() {
        if (currentIndex >= exercises.size()) {
            startActivity(new Intent(this, TrainingFinishedActivity.class));
            finish();
            return;
        }

        TrainingExercise exercise = exercises.get(currentIndex);
        int total = exercises.size();
        Fragment fragment;

        switch (exercise.getType()) {
            case A: {
                ExerciseTypeAFragment f = ExerciseTypeAFragment.newInstance(
                        exercise.getExerciseA(), currentIndex, total);
                f.setOnAnswerSubmittedListener(this::onAnswerSubmitted);
                fragment = f;
                break;
            }
            case B: {
                ExerciseTypeBFragment f = ExerciseTypeBFragment.newInstance(
                        exercise.getExerciseB(), currentIndex, total);
                f.setOnAnswerSubmittedListener(this::onAnswerSubmitted);
                fragment = f;
                break;
            }
            case C: {
                ExerciseTypeCFragment f = ExerciseTypeCFragment.newInstance(
                        exercise.getExerciseC(), currentIndex, total);
                f.setOnAnswerSubmittedListener(this::onAnswerSubmitted);
                fragment = f;
                break;
            }
            case D:
            default: {
                ExerciseTypeDFragment f = ExerciseTypeDFragment.newInstance(
                        exercise.getExerciseD(), currentIndex, total);
                f.setOnAnswerSubmittedListener(this::onAnswerSubmitted);
                fragment = f;
                break;
            }
        }

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.exerciseContainer, fragment);
        tx.commit();
    }

    private void onAnswerSubmitted() {
        currentIndex++;
        showCurrentExercise();
    }
}