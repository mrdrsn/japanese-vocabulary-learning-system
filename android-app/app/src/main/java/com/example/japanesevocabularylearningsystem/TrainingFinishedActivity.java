package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.japanesevocabularylearningsystem.network.ApiClient;
import com.example.japanesevocabularylearningsystem.network.dto.TrainingSessionDto;
import com.example.japanesevocabularylearningsystem.network.dto.TrainingSessionSaveDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingFinishedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_finished);

        int correctCount = getIntent().getIntExtra("EXTRA_CORRECT_COUNT", 0);
        int totalCount   = getIntent().getIntExtra("EXTRA_TOTAL_COUNT", 0);
        String scenarioId = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_ID);

        TextView tvScore    = findViewById(R.id.tvScore);
        TextView tvProgress = findViewById(R.id.tvProgress);

        tvScore.setText(correctCount + " / " + totalCount);

        TrainingSessionSaveDto body = new TrainingSessionSaveDto(
                scenarioId != null ? scenarioId : "unknown",
                correctCount,
                totalCount
        );

        ApiClient.getInstance().saveTrainingSession(body)
                .enqueue(new Callback<TrainingSessionDto>() {
                    @Override
                    public void onResponse(Call<TrainingSessionDto> call,
                                           Response<TrainingSessionDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            tvProgress.setText(buildProgressMessage(
                                    response.body().correctCount,
                                    response.body().previousCorrectCount
                            ));
                        }
                    }

                    @Override
                    public void onFailure(Call<TrainingSessionDto> call, Throwable t) {
                        Toast.makeText(TrainingFinishedActivity.this,
                                "Не удалось сохранить результат", Toast.LENGTH_SHORT).show();
                    }
                });

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingFinishedActivity.this, TrainingMenuActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_ID,
                    getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_ID));
            intent.putExtra(MainActivity.EXTRA_SCENARIO_NAME,
                    getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_NAME));
            startActivity(intent);
            finish();
        });
    }

    private String buildProgressMessage(int current, Integer previous) {
        if (previous == null) return "Это ваша первая тренировка!";
        int diff = current - previous;
        if (diff == 0) return "Такой же результат, как в прошлый раз";
        if (diff > 0) return "На " + diff + " " + answerWord(diff) + " больше, чем в прошлый раз!";
        return "На " + Math.abs(diff) + " " + answerWord(Math.abs(diff)) + " меньше, чем в прошлый раз";
    }

    private String answerWord(int n) {
        int mod10 = n % 10, mod100 = n % 100;
        if (mod10 == 1 && mod100 != 11) return "ответ";
        if (mod10 >= 2 && mod10 <= 4 && (mod100 < 10 || mod100 >= 20)) return "ответа";
        return "ответов";
    }
}