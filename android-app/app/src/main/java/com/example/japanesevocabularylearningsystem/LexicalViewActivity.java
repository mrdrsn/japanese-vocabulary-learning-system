package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japanesevocabularylearningsystem.adapter.UtteranceAdapter;
import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.model.ScenarioStep;
import com.example.japanesevocabularylearningsystem.model.Utterance;
import com.example.japanesevocabularylearningsystem.network.ApiClient;
import com.example.japanesevocabularylearningsystem.network.dto.FullLexiconDto;
import com.example.japanesevocabularylearningsystem.network.dto.LexicalItemDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LexicalViewActivity extends AppCompatActivity {

    public static final List<Utterance> allUtterances = new ArrayList<>();

    private UtteranceAdapter adapter;
    private final List<Utterance> utteranceList = new ArrayList<>();
    private final List<String> availableStepNames = new ArrayList<>();
    private ProgressBar progressBar;

    private String scenarioId;
    private String scenarioName;

    // Сохраняем между пересозданиями activity
    private static List<String> currentSelectedStepIds = null;
    private static String lastScenarioId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexical_view);

        scenarioId   = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_ID);
        scenarioName = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_NAME);
        if (scenarioId == null)   scenarioId   = "SC1";
        if (scenarioName == null) scenarioName = "Convenience Store";

        // Сменился сценарий → сбрасываем выбор шагов
        if (!scenarioId.equals(lastScenarioId)) {
            currentSelectedStepIds = null;
            lastScenarioId = scenarioId;
        }

        ImageView btnBack      = findViewById(R.id.btnBack);
        ImageView btnSettings  = findViewById(R.id.btnSettings);
        ImageView btnCardStart = findViewById(R.id.btnCardStart);
        RecyclerView rvUtterances = findViewById(R.id.rvUtterances);
        progressBar = findViewById(R.id.progressBar);

        adapter = new UtteranceAdapter(utteranceList, getSupportFragmentManager());
        rvUtterances.setLayoutManager(new LinearLayoutManager(this));
        rvUtterances.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, ModeChooseActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_ID, scenarioId);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_NAME, scenarioName);
            startActivity(intent);
            finish();
        });

        btnSettings.setOnClickListener(v -> {
            if (availableStepNames.isEmpty()) return;
            ArrayList<ScenarioStep> steps = new ArrayList<>();
            for (String name : availableStepNames) {
                boolean selected = currentSelectedStepIds == null
                        || currentSelectedStepIds.contains(name);
                steps.add(new ScenarioStep(name, name, selected));
            }
            ScenarioStepsBottomSheet sheet =
                    ScenarioStepsBottomSheet.newInstance(scenarioName, steps);
            sheet.setOnStepsAppliedListener(selectedNames -> {
                currentSelectedStepIds = new ArrayList<>(selectedNames);
                adapter.updateData(filterBySteps(selectedNames));
                CardLearnActivity.clearSavedState();
            });
            sheet.show(getSupportFragmentManager(), "steps_sheet");
        });

        btnCardStart.setOnClickListener(v -> {
            List<Utterance> cardsToStudy = currentSelectedStepIds == null
                    ? getStudyableItems()
                    : filterBySteps(currentSelectedStepIds);
            CardLearnActivity.pendingUtterances = cardsToStudy;
            Intent intent = new Intent(this, CardLearnActivity.class);
            if (currentSelectedStepIds != null) {
                intent.putStringArrayListExtra("stepIds",
                        new ArrayList<>(currentSelectedStepIds));
            }
            startActivity(intent);
        });

        loadLexicon();
    }

    private void loadLexicon() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        ApiClient.getInstance().getFullLexicon(scenarioId)
                .enqueue(new Callback<FullLexiconDto>() {
                    @Override
                    public void onResponse(Call<FullLexiconDto> call,
                                           Response<FullLexiconDto> response) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().items != null) {
                            utteranceList.clear();
                            allUtterances.clear();
                            for (LexicalItemDto dto : response.body().items) {
                                utteranceList.add(toModel(dto));
                            }
                            allUtterances.addAll(utteranceList);
                            buildAvailableSteps();
                            if (currentSelectedStepIds != null) {
                                adapter.updateData(filterBySteps(currentSelectedStepIds));
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            fallbackToMock();
                        }
                    }

                    @Override
                    public void onFailure(Call<FullLexiconDto> call, Throwable t) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        fallbackToMock();
                    }
                });
    }

    // Собираем уникальные шаги из реальных данных в порядке появления
    private void buildAvailableSteps() {
        availableStepNames.clear();
        Set<String> seen = new LinkedHashSet<>();
        for (Utterance u : allUtterances) {
            if (u.getStepDisplayNames() != null) seen.addAll(u.getStepDisplayNames());
        }
        availableStepNames.addAll(seen);
    }

    private Utterance toModel(LexicalItemDto dto) {
        Utterance u = new Utterance();
        u.setId(dto.id);
        u.setSurfaceRomaji(dto.romaji);
        u.setTranslation(dto.translation);
        u.setType(dto.type);
        u.setStepDisplayNames(dto.stepDisplayNames);
        u.setRoleDisplayNames(dto.roleDisplayNames);
        u.setCommunicativeIntentNames(dto.communicativeIntentNames);
        u.setAudioUrl(dto.audioUrl);
        if (dto.examples != null) {
            List<Utterance.ExampleEntry> entries = new ArrayList<>();
            for (LexicalItemDto.ExampleEntryDto e : dto.examples) {
                Utterance.ExampleEntry ex = new Utterance.ExampleEntry(
                        e.luId, e.luRomaji, e.filledRomaji, e.luTranslation);
                ex.luId2 = e.luId2;
                ex.luRomaji2 = e.luRomaji2;
                entries.add(ex);
            }
            u.setExamples(entries);
        }
        return u;
    }

    private void fallbackToMock() {
        if ("SC1".equals(scenarioId)) {
            utteranceList.clear();
            utteranceList.addAll(MockDataProvider.getConvenienceStoreUtterances());
            allUtterances.clear();
            allUtterances.addAll(utteranceList);
            buildAvailableSteps();
        }
        if (currentSelectedStepIds != null) {
            adapter.updateData(filterBySteps(currentSelectedStepIds));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private List<Utterance> getStudyableItems() {
        List<Utterance> result = new ArrayList<>();
        for (Utterance u : allUtterances) {
            if (!"LEXICAL_UNIT".equals(u.getType())) result.add(u);
        }
        return result;
    }

    // selectedStepDisplayNames — список отображаемых названий шагов (не ID)
    private List<Utterance> filterBySteps(List<String> selectedStepDisplayNames) {
        Set<String> selectedNames = new HashSet<>(selectedStepDisplayNames);
        List<Utterance> result = new ArrayList<>();
        for (Utterance u : allUtterances) {
            if ("LEXICAL_UNIT".equals(u.getType())) continue;
            if (u.getStepDisplayNames() != null) {
                for (String name : u.getStepDisplayNames()) {
                    if (selectedNames.contains(name)) {
                        result.add(u);
                        break;
                    }
                }
            }
        }
        return result.isEmpty() ? getStudyableItems() : result;
    }
}