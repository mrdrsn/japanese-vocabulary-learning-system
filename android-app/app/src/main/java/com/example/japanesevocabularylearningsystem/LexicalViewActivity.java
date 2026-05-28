package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japanesevocabularylearningsystem.adapter.UtteranceAdapter;
import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.model.Utterance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LexicalViewActivity extends AppCompatActivity {

    private UtteranceAdapter adapter;
    private List<Utterance> allUtterances;
    private List<String> currentSelectedStepIds = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexical_view);

        ImageView btnBack     = findViewById(R.id.btnBack);
        ImageView btnSettings = findViewById(R.id.btnSettings);
        ImageView btnCardStart = findViewById(R.id.btnCardStart);
        RecyclerView rvUtterances = findViewById(R.id.rvUtterances);

        allUtterances = MockDataProvider.getConvenienceStoreUtterances();

        adapter = new UtteranceAdapter(allUtterances, getSupportFragmentManager());
        rvUtterances.setLayoutManager(new LinearLayoutManager(this));
        rvUtterances.setAdapter(adapter);

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
                // сброс прогресса карточного режима при смене шагов
                CardLearnActivity.clearSavedState();
            });

            sheet.show(getSupportFragmentManager(), "steps_sheet");
        });

        btnCardStart.setOnClickListener(v -> {
            // передаём только карточки из выбранных шагов (или все, если фильтр не задан)
            List<Utterance> cardsToStudy = currentSelectedStepIds == null
                    ? allUtterances
                    : filterBySteps(currentSelectedStepIds);

            ArrayList<String> utteranceIds = new ArrayList<>();
            for (Utterance u : cardsToStudy) utteranceIds.add(u.getId());

            Intent intent = new Intent(this, CardLearnActivity.class);
            intent.putStringArrayListExtra("utteranceIds", utteranceIds);
            if (currentSelectedStepIds != null) {
                intent.putStringArrayListExtra("stepIds",
                        new ArrayList<>(currentSelectedStepIds));
            }
            startActivity(intent);
        });
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
}