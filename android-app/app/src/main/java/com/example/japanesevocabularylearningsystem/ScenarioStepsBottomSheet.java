package com.example.japanesevocabularylearningsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japanesevocabularylearningsystem.adapter.ScenarioStepAdapter;
import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.model.ScenarioStep;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ScenarioStepsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_SCENARIO_NAME = "scenario_name";

    // Callback: возвращает ID выбранных шагов в LexicalViewActivity
    public interface OnStepsAppliedListener {
        void onStepsApplied(List<String> selectedStepIds);
    }

    private OnStepsAppliedListener listener;

    public static ScenarioStepsBottomSheet newInstance(String scenarioName) {
        ScenarioStepsBottomSheet sheet = new ScenarioStepsBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SCENARIO_NAME, scenarioName);
        sheet.setArguments(args);
        return sheet;
    }

    public void setOnStepsAppliedListener(OnStepsAppliedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scenario_steps_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String scenarioName = getArguments() != null
                ? getArguments().getString(ARG_SCENARIO_NAME, "Сценарий")
                : "Сценарий";

        TextView tvName = view.findViewById(R.id.tvScenarioName);
        tvName.setText(scenarioName);

        // Делаем глубокую копию шагов — чтобы закрытие без "Применить"
        // не сохраняло изменения
        List<ScenarioStep> stepsCopy = deepCopySteps(MockDataProvider.getConvenienceStoreSteps());

        RecyclerView rvSteps = view.findViewById(R.id.rvSteps);
        ScenarioStepAdapter adapter = new ScenarioStepAdapter(stepsCopy);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSteps.setLayoutManager(layoutManager);
        rvSteps.addItemDecoration(new SpaceItemDecoration(20)); // 20dp между элементами
        rvSteps.setAdapter(adapter);

        // Закрыть без сохранения
        view.findViewById(R.id.btnCloseSteps).setOnClickListener(v -> dismiss());

        // Применить — передаём выбранные ID и закрываем
        AppCompatButton btnApply = view.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(v -> {
            List<String> selectedIds = new ArrayList<>();
            for (ScenarioStep step : adapter.getSteps()) {
                if (step.isSelected()) {
                    selectedIds.add(step.getId());
                }
            }
            if (listener != null) {
                listener.onStepsApplied(selectedIds);
            }
            dismiss();
        });
    }

    private List<ScenarioStep> deepCopySteps(List<ScenarioStep> original) {
        List<ScenarioStep> copy = new ArrayList<>();
        for (ScenarioStep step : original) {
            copy.add(new ScenarioStep(step.getId(), step.getName(), step.isSelected()));
        }
        return copy;
    }
}