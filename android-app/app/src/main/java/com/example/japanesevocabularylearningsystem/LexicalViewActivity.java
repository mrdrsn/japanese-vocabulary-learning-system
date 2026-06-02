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
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LexicalViewActivity extends AppCompatActivity {

    public static final List<Utterance> allUtterances = new ArrayList<>();

    private UtteranceAdapter adapter;
    private final List<Utterance> utteranceList = new ArrayList<>();
    private ProgressBar progressBar;
    private static List<String> currentSelectedStepIds = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexical_view);

        ImageView btnBack      = findViewById(R.id.btnBack);
        ImageView btnSettings  = findViewById(R.id.btnSettings);
        ImageView btnCardStart = findViewById(R.id.btnCardStart);
        RecyclerView rvUtterances = findViewById(R.id.rvUtterances);
        progressBar = findViewById(R.id.progressBar);

        adapter = new UtteranceAdapter(utteranceList, getSupportFragmentManager());
        rvUtterances.setLayoutManager(new LinearLayoutManager(this));
        rvUtterances.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, ModeChooseActivity.class));
            finish();
        });

        btnSettings.setOnClickListener(v -> {
            ArrayList<ScenarioStep> steps = new ArrayList<>();
            for (ScenarioStep step : MockDataProvider.getConvenienceStoreSteps()) {
                boolean selected = currentSelectedStepIds == null
                        || currentSelectedStepIds.contains(step.getId());
                steps.add(new ScenarioStep(step.getId(), step.getName(), selected));
            }
            ScenarioStepsBottomSheet sheet =
                    ScenarioStepsBottomSheet.newInstance("Convenience Store", steps);
            sheet.setOnStepsAppliedListener(selectedStepIds -> {
                currentSelectedStepIds = new ArrayList<>(selectedStepIds);
                adapter.updateData(filterBySteps(selectedStepIds));
                CardLearnActivity.clearSavedState();
            });
            sheet.show(getSupportFragmentManager(), "steps_sheet");
        });

        btnCardStart.setOnClickListener(v -> {
            List<Utterance> cardsToStudy = currentSelectedStepIds == null
                    ? getStudyableItems()
                    : filterBySteps(currentSelectedStepIds);

            CardLearnActivity.pendingUtterances = cardsToStudy;  // ← передаём актуальные данные

            Intent intent = new Intent(this, CardLearnActivity.class);
            if (currentSelectedStepIds != null) {
                intent.putStringArrayListExtra("stepIds", new ArrayList<>(currentSelectedStepIds));
            }
            startActivity(intent);
        });

        loadLexicon();
    }

    private void loadLexicon() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        ApiClient.getInstance().getFullLexicon("SC1")
                .enqueue(new Callback<FullLexiconDto>() {
                    @Override
                    public void onResponse(Call<FullLexiconDto> call, Response<FullLexiconDto> response) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().items != null) {
                            utteranceList.clear();
                            allUtterances.clear();
                            for (LexicalItemDto dto : response.body().items) {
                                utteranceList.add(toModel(dto));
                            }
                            allUtterances.addAll(utteranceList);
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
        utteranceList.clear();
        utteranceList.addAll(MockDataProvider.getConvenienceStoreUtterances());
        allUtterances.clear();
        allUtterances.addAll(utteranceList);
        if (currentSelectedStepIds != null) {
            adapter.updateData(filterBySteps(currentSelectedStepIds));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    // Только UTTERANCE и TEMPLATE для карточного режима
    private List<Utterance> getStudyableItems() {
        List<Utterance> result = new ArrayList<>();
        for (Utterance u : allUtterances) {
            if (!"LEXICAL_UNIT".equals(u.getType())) result.add(u);
        }
        return result;
    }

    private List<Utterance> filterBySteps(List<String> selectedStepIds) {
        Set<String> selectedNames = new HashSet<>();
        for (ScenarioStep step : MockDataProvider.getConvenienceStoreSteps()) {
            if (selectedStepIds.contains(step.getId())) {
                selectedNames.add(step.getName());
            }
        }

        List<Utterance> result = new ArrayList<>();
        for (Utterance u : allUtterances) {          // ← было utteranceList
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