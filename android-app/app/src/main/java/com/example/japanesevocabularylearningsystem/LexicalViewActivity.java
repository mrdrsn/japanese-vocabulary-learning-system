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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexical_view);

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView btnSettings = findViewById(R.id.btnSettings);
        RecyclerView rvUtterances = findViewById(R.id.rvUtterances);

        allUtterances = MockDataProvider.getConvenienceStoreUtterances();

        adapter = new UtteranceAdapter(new ArrayList<>(allUtterances));
        rvUtterances.setLayoutManager(new LinearLayoutManager(this));
        rvUtterances.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, ModeChooseActivity.class));
            finish();
        });

        btnSettings.setOnClickListener(v -> {
            ScenarioStepsBottomSheet sheet =
                    ScenarioStepsBottomSheet.newInstance("Convenience Store");

            sheet.setOnStepsAppliedListener(selectedStepIds -> {
                // Фильтруем utterances по выбранным шагам
                List<Utterance> filtered = filterBySteps(selectedStepIds);
                adapter.updateData(filtered);
            });

            sheet.show(getSupportFragmentManager(), "steps_sheet");
        });
    }

    private List<Utterance> filterBySteps(List<String> selectedStepIds) {
        Map<String, List<String>> stepMap = MockDataProvider.getConvenienceStoreStepMap();

        // Собираем все utterance ID для выбранных шагов
        List<String> allowedIds = new ArrayList<>();
        for (String stepId : selectedStepIds) {
            List<String> ids = stepMap.get(stepId);
            if (ids != null) allowedIds.addAll(ids);
        }

        // Оставляем только те utterances, чьи ID в списке
        List<Utterance> result = new ArrayList<>();
        for (Utterance u : allUtterances) {
            if (allowedIds.contains(u.getId())) {
                result.add(u);
            }
        }

        // Если ничего не нашлось — показываем всё (защита)
        return result.isEmpty() ? new ArrayList<>(allUtterances) : result;
    }
}