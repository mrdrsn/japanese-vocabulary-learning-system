package com.example.japanesevocabularylearningsystem.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.japanesevocabularylearningsystem.R;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeA;

import java.util.ArrayList;

public class ExerciseTypeAFragment extends Fragment {

    private static final int COLOR_ACCENT  = 0xFF8E94FF;
    private static final int COLOR_CORRECT = 0xFF4CAF50;
    private static final int COLOR_WRONG   = 0xFFF44336;
    private static final int COLOR_DEFAULT = 0xFFFFFFFF;

    private static final String ARG_INSTRUCTION    = "instruction";
    private static final String ARG_TRANSLATION    = "translation";
    private static final String ARG_ROMAJI         = "romaji";
    private static final String ARG_ROLE_NAME      = "roleName";
    private static final String ARG_OPTIONS        = "options";
    private static final String ARG_CORRECT_FLAGS  = "correctFlags";
    private static final String ARG_INDEX          = "index";
    private static final String ARG_TOTAL          = "total";

    private OnAnswerSubmittedListener listener;
    private AppCompatButton[] optionButtons;
    private AppCompatButton btnSubmit;

    private boolean submitted = false;
    private final java.util.Set<Integer> selectedIndices = new java.util.HashSet<>();

    public static ExerciseTypeAFragment newInstance(ExerciseTypeA exercise, int index, int total) {
        ExerciseTypeAFragment fragment = new ExerciseTypeAFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INSTRUCTION, exercise.getInstruction());
        args.putString(ARG_TRANSLATION, exercise.getTranslation());
        args.putString(ARG_ROMAJI, exercise.getRomajiWithGap());
        args.putString(ARG_ROLE_NAME, exercise.getRoleName());
        args.putStringArrayList(ARG_OPTIONS, new ArrayList<>(exercise.getOptions()));
        // Convert List<Boolean> to boolean[]
        java.util.List<Boolean> flags = exercise.getCorrectFlags();
        boolean[] flagsArr = new boolean[flags != null ? flags.size() : 0];
        if (flags != null) {
            for (int i = 0; i < flags.size(); i++) flagsArr[i] = Boolean.TRUE.equals(flags.get(i));
        }
        args.putBooleanArray(ARG_CORRECT_FLAGS, flagsArr);
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

        int index = args.getInt(ARG_INDEX);
        int total = args.getInt(ARG_TOTAL);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        TextView tvCounter = view.findViewById(R.id.tvCounter);
        android.widget.ProgressBar progressBar = view.findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> requireActivity().finish());
        tvCounter.setText((index + 1) + "/" + total);
        progressBar.setMax(total);
        progressBar.setProgress(index + 1);

        TextView tvInstruction = view.findViewById(R.id.tvInstruction);
        tvInstruction.setText(args.getString(ARG_INSTRUCTION));
        tvInstruction.setBackground(makePill(COLOR_ACCENT, 24));

        view.<TextView>findViewById(R.id.tvTranslation).setText(args.getString(ARG_TRANSLATION));
        view.<TextView>findViewById(R.id.tvRomajiGap).setText(args.getString(ARG_ROMAJI));
        view.<TextView>findViewById(R.id.tvRoleValue).setText(args.getString(ARG_ROLE_NAME));

        java.util.List<String> options = args.getStringArrayList(ARG_OPTIONS);
        btnSubmit = view.findViewById(R.id.btnSubmitAnswer);
        optionButtons = new AppCompatButton[]{
                view.findViewById(R.id.btnOption1),
                view.findViewById(R.id.btnOption2),
                view.findViewById(R.id.btnOption3),
                view.findViewById(R.id.btnOption4)
        };

        for (int i = 0; i < optionButtons.length; i++) {
            AppCompatButton btn = optionButtons[i];
            if (options != null && i < options.size()) {
                btn.setText(options.get(i));
                btn.setVisibility(View.VISIBLE);
                final int idx = i;
                btn.setOnClickListener(v -> selectOption(idx));
            } else {
                btn.setVisibility(View.GONE);
            }
        }

        btnSubmit.setOnClickListener(v -> {
            if (!submitted) {
                submitted = true;
                showFeedback(args.getBooleanArray(ARG_CORRECT_FLAGS));
                btnSubmit.setText("Продолжить");
            } else {
                if (listener != null) listener.onAnswerSubmitted();
            }
        });
    }

    private void selectOption(int index) {
        if (submitted) return;
        // Переключаем выбор: повторное нажатие снимает выделение
        if (selectedIndices.contains(index)) {
            selectedIndices.remove(index);
            optionButtons[index].setBackground(makePill(COLOR_DEFAULT, 14));
            optionButtons[index].setTextColor(0xFF1A1A1A);
        } else {
            selectedIndices.add(index);
            optionButtons[index].setBackground(makePill(COLOR_ACCENT, 14));
            optionButtons[index].setTextColor(0xFFFFFFFF);
        }
        btnSubmit.setVisibility(selectedIndices.isEmpty() ? View.GONE : View.VISIBLE);
    }
    private void showFeedback(boolean[] correctFlags) {
        if (correctFlags == null) return;
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].getVisibility() != View.VISIBLE) continue;
            boolean isCorrect = i < correctFlags.length && correctFlags[i];
            boolean isSelected = selectedIndices.contains(i);
            if (isCorrect) {
                // Все правильные варианты → зелёные
                optionButtons[i].setBackground(makePill(COLOR_CORRECT, 14));
                optionButtons[i].setTextColor(0xFFFFFFFF);
            } else if (isSelected) {
                // Выбранный неправильный вариант → красный
                optionButtons[i].setBackground(makePill(COLOR_WRONG, 14));
                optionButtons[i].setTextColor(0xFFFFFFFF);
            } else {
                optionButtons[i].setBackground(makePill(COLOR_DEFAULT, 14));
                optionButtons[i].setTextColor(0xFF1A1A1A);
            }
            optionButtons[i].setClickable(false);
        }
    }

    private GradientDrawable makePill(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(dpToPx(radiusDp));
        d.setColor(color);
        return d;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}