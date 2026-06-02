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
import com.example.japanesevocabularylearningsystem.model.ScenarioStep;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ScenarioStepsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_SCENARIO_NAME = "scenario_name";
    private static final String ARG_STEPS = "steps";

    public interface OnStepsAppliedListener {
        void onStepsApplied(List<String> selectedStepIds);
    }

    private OnStepsAppliedListener listener;

    public static ScenarioStepsBottomSheet newInstance(String scenarioName,
                                                       ArrayList<ScenarioStep> steps) {
        ScenarioStepsBottomSheet sheet = new ScenarioStepsBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_SCENARIO_NAME, scenarioName);
        args.putSerializable(ARG_STEPS, steps);
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

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = requireArguments();
        String scenarioName = args.getString(ARG_SCENARIO_NAME, "Сценарий");
        List<ScenarioStep> steps = (List<ScenarioStep>) args.getSerializable(ARG_STEPS);
        if (steps == null) steps = new ArrayList<>();

        TextView tvName = view.findViewById(R.id.tvScenarioName);
        tvName.setText(scenarioName);

        RecyclerView rvSteps = view.findViewById(R.id.rvSteps);
        ScenarioStepAdapter adapter = new ScenarioStepAdapter(steps);
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
}