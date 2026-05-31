package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japanesevocabularylearningsystem.adapter.UtteranceAdapter;
import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.model.Utterance;
import com.example.japanesevocabularylearningsystem.network.ApiClient;
import com.example.japanesevocabularylearningsystem.network.dto.UtteranceDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LexicalViewActivity extends AppCompatActivity {

    private UtteranceAdapter adapter;
    private ProgressBar progressBar;
    private List<Utterance> allUtterances = new ArrayList<>();
    private List<String> currentSelectedStepIds = null;
    private ImageView btnBack;
    private ImageView btnSettings;
    private ImageView btnCardStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexical_view);
        progressBar = findViewById(R.id.progressBar);

        btnBack      = findViewById(R.id.btnBack);
        btnSettings  = findViewById(R.id.btnSettings);
        btnCardStart = findViewById(R.id.btnCardStart);
        RecyclerView rvUtterances = findViewById(R.id.rvUtterances);

        adapter = new UtteranceAdapter(allUtterances, getSupportFragmentManager());
        rvUtterances.setLayoutManager(new LinearLayoutManager(this));
        rvUtterances.setAdapter(adapter);

        loadLexicon();

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, ModeChooseActivity.class));
            finish();
        });

        btnSettings.setOnClickListener(v -> {
            ScenarioStepsBottomSheet sheet =
                    ScenarioStepsBottomSheet.newInstance("Convenience Store", currentSelectedStepIds);

            sheet.setOnStepsAppliedListener(selectedStepIds -> {
                currentSelectedStepIds = new ArrayList<>(selectedStepIds);
                adapter.updateData(filterBySteps(selectedStepIds));
                CardLearnActivity.clearSavedState();
            });

            sheet.show(getSupportFragmentManager(), "steps_sheet");
        });

        btnCardStart.setOnClickListener(v -> {
            List<Utterance> cardsToStudy = currentSelectedStepIds == null
                    ? allUtterances
                    : filterBySteps(currentSelectedStepIds);

            ArrayList<String> utteranceIds = new ArrayList<>();
            for (Utterance u : cardsToStudy) utteranceIds.add(u.getId());

            Intent intent = new Intent(this, CardLearnActivity.class);
            intent.putStringArrayListExtra("utteranceIds", utteranceIds);
            if (currentSelectedStepIds != null) {
                intent.putStringArrayListExtra("stepIds", new ArrayList<>(currentSelectedStepIds));
            }
            startActivity(intent);
        });
    }

    private void loadLexicon() {
        setButtonsEnabled(false);
        ApiClient.getInstance().getLexicon("SC1").enqueue(new Callback<List<UtteranceDto>>() {
            @Override
            public void onResponse(Call<List<UtteranceDto>> call, Response<List<UtteranceDto>> response) {
                progressBar.setVisibility(View.GONE);
                setButtonsEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    allUtterances.clear();
                    for (UtteranceDto dto : response.body()) {
                        Utterance u = new Utterance(dto.id, dto.romaji, dto.ruTranslation, false);
                        if (dto.roles != null && !dto.roles.isEmpty()) {
                            u.setRoleId(dto.roles.get(0).id);
                        }
                        allUtterances.add(u);
                    }
                    adapter.updateData(new ArrayList<>(allUtterances));
                } else {
                    fallbackToMock();
                }
            }

            @Override
            public void onFailure(Call<List<UtteranceDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                setButtonsEnabled(true);
                Toast.makeText(LexicalViewActivity.this,
                        "Нет соединения с сервером, загружены тестовые данные", Toast.LENGTH_SHORT).show();
                fallbackToMock();
            }
        });
    }

    private void fallbackToMock() {
        allUtterances.clear();
        allUtterances.addAll(MockDataProvider.getConvenienceStoreUtterances());
        adapter.updateData(new ArrayList<>(allUtterances));
    }

    private List<Utterance> filterBySteps(List<String> selectedStepIds) {
        Map<String, List<String>> stepMap = MockDataProvider.getConvenienceStoreStepMap();

        List<String> allowedIds = new ArrayList<>();
        for (String stepId : selectedStepIds) {
            List<String> ids = stepMap.get(stepId);
            if (ids != null) allowedIds.addAll(ids);
        }

        List<Utterance> result = new ArrayList<>();
        for (Utterance u : allUtterances) {
            if (allowedIds.contains(u.getId())) result.add(u);
        }

        return result.isEmpty() ? new ArrayList<>(allUtterances) : result;
    }
    private void setButtonsEnabled(boolean enabled) {
        float alpha = enabled ? 1.0f : 0.4f;
        btnBack.setEnabled(enabled);
        btnBack.setAlpha(alpha);
        btnSettings.setEnabled(enabled);
        btnSettings.setAlpha(alpha);
        btnCardStart.setEnabled(enabled);
        btnCardStart.setAlpha(alpha);
    }
}