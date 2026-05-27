package com.example.japanesevocabularylearningsystem.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.japanesevocabularylearningsystem.R;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeA;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTypeAFragment extends Fragment {

    private static final String ARG_INSTRUCTION = "instruction";
    private static final String ARG_TRANSLATION = "translation";
    private static final String ARG_ROMAJI = "romaji";
    private static final String ARG_OPTIONS = "options";
    private static final String ARG_INDEX = "index";
    private static final String ARG_TOTAL = "total";

    public interface OnAnswerSubmittedListener {
        void onAnswerSubmitted();
    }

    private OnAnswerSubmittedListener listener;
    private AppCompatButton[] optionButtons;
    private AppCompatButton btnSubmit;

    public static ExerciseTypeAFragment newInstance(ExerciseTypeA exercise,
                                                    int index,
                                                    int total) {
        ExerciseTypeAFragment fragment = new ExerciseTypeAFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INSTRUCTION, exercise.getInstruction());
        args.putString(ARG_TRANSLATION, exercise.getTranslation());
        args.putString(ARG_ROMAJI, exercise.getRomajiWithGap());
        args.putStringArrayList(ARG_OPTIONS, new ArrayList<>(exercise.getOptions()));
        args.putInt(ARG_INDEX, index);
        args.putInt(ARG_TOTAL, total);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnAnswerSubmittedListener(OnAnswerSubmittedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_type_a, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;

        TextView tvCounter = view.findViewById(R.id.tvExerciseCounter);
        TextView tvInstruction = view.findViewById(R.id.tvInstruction);
        TextView tvTranslation = view.findViewById(R.id.tvTranslation);
        TextView tvRomaji = view.findViewById(R.id.tvRomajiGap);
        LinearLayout indicatorContainer = view.findViewById(R.id.indicatorContainer);
        btnSubmit = view.findViewById(R.id.btnSubmitAnswer);

        int index = args.getInt(ARG_INDEX);
        int total = args.getInt(ARG_TOTAL);

        tvCounter.setText(getString(R.string.exercise_counter_format, index + 1, total));
        tvInstruction.setText(args.getString(ARG_INSTRUCTION));
        tvTranslation.setText(args.getString(ARG_TRANSLATION));
        tvRomaji.setText(args.getString(ARG_ROMAJI));

        renderIndicators(indicatorContainer, index, total);
        renderOptions(view, args.getStringArrayList(ARG_OPTIONS));

        btnSubmit.setVisibility(View.GONE);
        btnSubmit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnswerSubmitted();
            }
        });
    }

    private void renderIndicators(LinearLayout container, int currentIndex, int total) {
        Context ctx = requireContext();
        container.removeAllViews();
        int widthPx = dpToPx(30);
        int heightPx = dpToPx(10);
        int gapPx = dpToPx(10);

        for (int i = 0; i < total; i++) {
            View dot = new View(ctx);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthPx, heightPx);
            if (i > 0) params.leftMargin = gapPx;
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i <= currentIndex
                    ? R.drawable.bg_indicator_red
                    : R.drawable.bg_indicator_grey);
            container.addView(dot);
        }
    }

    private void renderOptions(View root, List<String> options) {
        optionButtons = new AppCompatButton[]{
                root.findViewById(R.id.btnOption1),
                root.findViewById(R.id.btnOption2),
                root.findViewById(R.id.btnOption3),
                root.findViewById(R.id.btnOption4)
        };

        if (options == null) return;

        for (int i = 0; i < optionButtons.length; i++) {
            AppCompatButton button = optionButtons[i];
            if (i < options.size()) {
                button.setText(options.get(i));
                button.setVisibility(View.VISIBLE);
                button.setOnClickListener(v -> selectOption(button));
            } else {
                button.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void selectOption(AppCompatButton selected) {
        for (AppCompatButton button : optionButtons) {
            button.setBackgroundResource(R.drawable.bg_option_default);
            button.setTextColor(0xFF000000);
        }
        selected.setBackgroundResource(R.drawable.bg_option_selected);
        selected.setTextColor(0xFFFFFFFF);
        btnSubmit.setVisibility(View.VISIBLE);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}