package com.example.japanesevocabularylearningsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japanesevocabularylearningsystem.R;
import com.example.japanesevocabularylearningsystem.model.ScenarioStep;

import java.util.List;

public class ScenarioStepAdapter extends RecyclerView.Adapter<ScenarioStepAdapter.StepViewHolder> {

    private final List<ScenarioStep> steps;

    public ScenarioStepAdapter(List<ScenarioStep> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scenario_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        ScenarioStep step = steps.get(position);

        holder.tvStepName.setText(step.getName());
        updateCheckboxVisual(holder, step.isSelected());

        holder.itemView.setOnClickListener(v -> {
            // Нельзя снять галочку с последнего выбранного шага
            if (step.isSelected() && countSelected() <= 1) return;

            step.setSelected(!step.isSelected());
            updateCheckboxVisual(holder, step.isSelected());
        });
    }

    private void updateCheckboxVisual(StepViewHolder holder, boolean isSelected) {
        holder.circleUnchecked.setVisibility(isSelected ? View.GONE : View.VISIBLE);
        holder.circleChecked.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    }

    private int countSelected() {
        int count = 0;
        for (ScenarioStep step : steps) {
            if (step.isSelected()) count++;
        }
        return count;
    }

    public List<ScenarioStep> getSteps() {
        return steps;
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView tvStepName;
        View circleUnchecked;
        FrameLayout circleChecked;

        StepViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStepName = itemView.findViewById(R.id.tvStepName);
            circleUnchecked = itemView.findViewById(R.id.circleUnchecked);
            circleChecked = itemView.findViewById(R.id.circleChecked);
        }
    }
}