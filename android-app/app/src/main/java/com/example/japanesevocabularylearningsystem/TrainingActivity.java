package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.japanesevocabularylearningsystem.fragment.ExerciseTypeAFragment;
import com.example.japanesevocabularylearningsystem.fragment.ExerciseTypeBFragment;
import com.example.japanesevocabularylearningsystem.fragment.ExerciseTypeCFragment;
import com.example.japanesevocabularylearningsystem.fragment.ExerciseTypeDFragment;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeA;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeB;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeC;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeD;
import com.example.japanesevocabularylearningsystem.network.ApiClient;
import com.example.japanesevocabularylearningsystem.network.dto.ExerciseTypeADto;
import com.example.japanesevocabularylearningsystem.network.dto.ExerciseTypeBDto;
import com.example.japanesevocabularylearningsystem.network.dto.ExerciseTypeCDto;
import com.example.japanesevocabularylearningsystem.network.dto.ExerciseTypeDDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingActivity extends AppCompatActivity {

    private static final int PER_TYPE    = 2;
    private static final int TOTAL_TYPES = 4;

    private String scenarioId;
    private int currentIndex = 0;
    private int correctCount    = 0;
    private int totalExercises  = 0;

    private final List<ExerciseTypeA> listA = new ArrayList<>();
    private final List<ExerciseTypeB> listB = new ArrayList<>();
    private final List<ExerciseTypeC> listC = new ArrayList<>();
    private final List<ExerciseTypeD> listD = new ArrayList<>();
    private final AtomicInteger loadedCount = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        scenarioId = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_ID);
        if (scenarioId == null) scenarioId = "SC1";
        if (savedInstanceState == null) loadAll();
    }

    private void loadAll() {
        ApiClient.getInstance()
                .getTypeAExercises(scenarioId, PER_TYPE, "auto")
                .enqueue(new Callback<List<ExerciseTypeADto>>() {
                    @Override
                    public void onResponse(Call<List<ExerciseTypeADto>> call,
                                           Response<List<ExerciseTypeADto>> response) {
                        if (response.isSuccessful() && response.body() != null)
                            for (ExerciseTypeADto dto : response.body()) listA.add(dto.toModel());
                        onTypeLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<ExerciseTypeADto>> call, Throwable t) {
                        onTypeLoaded();
                    }
                });

        ApiClient.getInstance()
                .getTypeBExercises(scenarioId, PER_TYPE, "auto")
                .enqueue(new Callback<List<ExerciseTypeBDto>>() {
                    @Override
                    public void onResponse(Call<List<ExerciseTypeBDto>> call,
                                           Response<List<ExerciseTypeBDto>> response) {
                        if (response.isSuccessful() && response.body() != null)
                            for (ExerciseTypeBDto dto : response.body()) listB.add(dto.toModel());
                        onTypeLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<ExerciseTypeBDto>> call, Throwable t) {
                        onTypeLoaded();
                    }
                });

        ApiClient.getInstance()
                .getTypeCExercises(scenarioId, PER_TYPE)
                .enqueue(new Callback<List<ExerciseTypeCDto>>() {
                    @Override
                    public void onResponse(Call<List<ExerciseTypeCDto>> call,
                                           Response<List<ExerciseTypeCDto>> response) {
                        if (response.isSuccessful() && response.body() != null)
                            for (ExerciseTypeCDto dto : response.body()) listC.add(dto.toModel());
                        onTypeLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<ExerciseTypeCDto>> call, Throwable t) {
                        onTypeLoaded();
                    }
                });

        ApiClient.getInstance()
                .getTypeDExercises(scenarioId, PER_TYPE)
                .enqueue(new Callback<List<ExerciseTypeDDto>>() {
                    @Override
                    public void onResponse(Call<List<ExerciseTypeDDto>> call,
                                           Response<List<ExerciseTypeDDto>> response) {
                        if (response.isSuccessful() && response.body() != null)
                            for (ExerciseTypeDDto dto : response.body()) listD.add(dto.toModel());
                        onTypeLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<ExerciseTypeDDto>> call, Throwable t) {
                        onTypeLoaded();
                    }
                });
    }

    private void onTypeLoaded() {
        if (loadedCount.incrementAndGet() == TOTAL_TYPES) {
            runOnUiThread(this::startSession);
        }
    }

    // Количество полных раундов A,B,C,D — ограничено наименее заполненным типом
    private int sessionSize() {
        int rounds = Math.min(PER_TYPE, Math.min(
                Math.min(listA.size(), listB.size()),
                Math.min(listC.size(), listD.size())));
        return rounds * TOTAL_TYPES;
    }

    private void startSession() {
        totalExercises = sessionSize();
        if (totalExercises == 0) {
            Toast.makeText(this, "Нет упражнений", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        showCurrentExercise();
    }

    private void showCurrentExercise() {
        int total = totalExercises;
        if (currentIndex >= totalExercises) {
            Intent intent = new Intent(this, TrainingFinishedActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_ID, scenarioId);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_NAME,
                    getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_NAME));
            intent.putExtra("EXTRA_CORRECT_COUNT", correctCount);
            intent.putExtra("EXTRA_TOTAL_COUNT", totalExercises);
            startActivity(intent);
            finish();
            return;
        }

        // currentIndex % 4: 0→A, 1→B, 2→C, 3→D
        // currentIndex / 4: 0→первый, 1→второй
        int typeIndex = currentIndex % TOTAL_TYPES;
        int round     = currentIndex / TOTAL_TYPES;

        Fragment f;
        switch (typeIndex) {
            case 0: {
                ExerciseTypeAFragment fa = ExerciseTypeAFragment.newInstance(
                        listA.get(round), currentIndex, total);
                fa.setOnAnswerSubmittedListener(this::onAnswerSubmitted);
                f = fa;
                break;
            }
            case 1: {
                ExerciseTypeBFragment fb = ExerciseTypeBFragment.newInstance(
                        listB.get(round), currentIndex, total);
                fb.setOnAnswerSubmittedListener(this::onAnswerSubmitted);
                f = fb;
                break;
            }
            case 2: {
                ExerciseTypeCFragment fc = ExerciseTypeCFragment.newInstance(
                        listC.get(round), currentIndex, total);
                fc.setOnAnswerSubmittedListener(this::onAnswerSubmitted);
                f = fc;
                break;
            }
            default: {
                ExerciseTypeDFragment fd = ExerciseTypeDFragment.newInstance(
                        listD.get(round), currentIndex, total);
                fd.setOnAnswerSubmittedListener(this::onAnswerSubmitted);
                f = fd;
                break;
            }
        }

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.exerciseContainer, f);
        tx.commit();
    }

    private void onAnswerSubmitted(boolean correct) {
        if (correct) correctCount++;
        currentIndex++;
        showCurrentExercise();
    }
}