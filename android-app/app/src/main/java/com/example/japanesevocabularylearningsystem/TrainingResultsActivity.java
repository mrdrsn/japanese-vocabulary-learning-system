package com.example.japanesevocabularylearningsystem;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.japanesevocabularylearningsystem.network.ApiClient;
import com.example.japanesevocabularylearningsystem.network.dto.TrainingSessionDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingResultsActivity extends AppCompatActivity {

    private LinearLayout resultsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_results);

        String scenarioId   = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_ID);
        String scenarioName = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_NAME);

        resultsContainer = findViewById(R.id.resultsContainer);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tvTitle);
        if (scenarioName != null && !scenarioName.isEmpty()) {
            tvTitle.setText("Результаты тренировок по сценарию " + scenarioName);
        }

        if (scenarioId == null || scenarioId.isEmpty()) {
            showEmpty();
            return;
        }

        loadResults(scenarioId);
    }

    private void loadResults(String scenarioId) {
        ApiClient.getInstance().getRecentSessions(scenarioId)
                .enqueue(new Callback<List<TrainingSessionDto>>() {
                    @Override
                    public void onResponse(Call<List<TrainingSessionDto>> call,
                                           Response<List<TrainingSessionDto>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            populateResults(response.body());
                        } else {
                            showEmpty();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TrainingSessionDto>> call, Throwable t) {
                        Toast.makeText(TrainingResultsActivity.this,
                                "Ошибка загрузки: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        showEmpty();
                    }
                });
    }

    private void populateResults(List<TrainingSessionDto> sessions) {
        resultsContainer.removeAllViews();
        if (sessions.isEmpty()) { showEmpty(); return; }

        for (TrainingSessionDto s : sessions) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, dpToPx(10));
            row.setLayoutParams(rowParams);

            TextView tvDate = new TextView(this);
            tvDate.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
            tvDate.setText(s.finishedAt);
            row.addView(tvDate);

            TextView tvResult = new TextView(this);
            tvResult.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            tvResult.setText(s.correctCount + " / " + s.totalCount);
            row.addView(tvResult);

            resultsContainer.addView(row);
        }
    }

    private void showEmpty() {
        resultsContainer.removeAllViews();
        TextView tv = new TextView(this);
        tv.setText("Нет результатов");
        tv.setGravity(Gravity.CENTER);
        resultsContainer.addView(tv);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}