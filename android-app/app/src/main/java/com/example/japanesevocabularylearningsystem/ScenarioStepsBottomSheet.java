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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ScenarioStepsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_SCENARIO_NAME = "scenario_name";
    private static final String ARG_SELECTED_IDS = "selected_ids";

    public interface OnStepsAppliedListener {
        void onStepsApplied(List<String> selectedStepIds);
    }

    private OnStepsAppliedListener listener;

    // selectedIds = null означает "все выбраны" (первый запуск)
    public static ScenarioStepsBottomSheet newInstance(String scenarioName,
                                                       @Nullable List<String> selectedIds) {
        ScenarioStepsBottomSheet sheet = new ScenarioStepsBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SCENARIO_NAME, scenarioName);
        if (selectedIds != null) {
            args.putStringArrayList(ARG_SELECTED_IDS, new ArrayList<>(selectedIds));
        }
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

        Bundle args = getArguments();
        String scenarioName = args != null ? args.getString(ARG_SCENARIO_NAME, "Сценарий") : "Сценарий";
        ArrayList<String> selectedIds = args != null ? args.getStringArrayList(ARG_SELECTED_IDS) : null;

        TextView tvName = view.findViewById(R.id.tvScenarioName);
        tvName.setText(scenarioName);

        // Если selectedIds == null — первый запуск, все выбраны
        // Иначе — восстанавливаем сохранённое состояние
        List<ScenarioStep> stepsCopy = deepCopyWithState(
                MockDataProvider.getConvenienceStoreSteps(), selectedIds);

        RecyclerView rvSteps = view.findViewById(R.id.rvSteps);
        ScenarioStepAdapter adapter = new ScenarioStepAdapter(stepsCopy);
        rvSteps.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSteps.addItemDecoration(new SpaceItemDecoration(20));
        rvSteps.setAdapter(adapter);

        view.findViewById(R.id.btnCloseSteps).setOnClickListener(v -> dismiss());

        AppCompatButton btnApply = view.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(v -> {
            List<String> result = new ArrayList<>();
            for (ScenarioStep step : adapter.getSteps()) {
                if (step.isSelected()) result.add(step.getId());
            }
            if (listener != null) listener.onStepsApplied(result);
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        }
    }

    // selectedIds == null → все выбраны; иначе — только те, чей id в списке
    private List<ScenarioStep> deepCopyWithState(List<ScenarioStep> original,
                                                 @Nullable List<String> selectedIds) {
        List<ScenarioStep> copy = new ArrayList<>();
        for (ScenarioStep step : original) {
            boolean selected = (selectedIds == null) || selectedIds.contains(step.getId());
            copy.add(new ScenarioStep(step.getId(), step.getName(), selected));
        }
        return copy;
    }
}