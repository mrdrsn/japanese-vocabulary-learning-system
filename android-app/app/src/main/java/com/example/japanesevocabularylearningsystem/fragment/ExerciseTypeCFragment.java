package com.example.japanesevocabularylearningsystem.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.japanesevocabularylearningsystem.R;
import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeC;
import com.example.japanesevocabularylearningsystem.model.Role;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTypeCFragment extends Fragment {

    private static final int ACCENT_COLOR = 0xFF9EAACF;

    private static final String ARG_UTTERANCE_ID = "utteranceId";
    private static final String ARG_CORRECT_ROLE_ID = "correctRoleId";
    private static final String ARG_OPTIONS = "options";
    private static final String ARG_INDEX = "index";
    private static final String ARG_TOTAL = "total";

    private OnAnswerSubmittedListener listener;
    private AppCompatButton[] optionButtons;
    private AppCompatButton btnSubmit;
    private LinearLayout btnRoleR1, btnRoleR2;
    private View circleR1, circleR2;
    private TextView tvRoleR1, tvRoleR2;
    private String selectedRoleId = null;
    private int selectedOptionIndex = -1;
    private String roleIdFirst, roleIdSecond;

    public static ExerciseTypeCFragment newInstance(ExerciseTypeC exercise, int index, int total) {
        ExerciseTypeCFragment fragment = new ExerciseTypeCFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UTTERANCE_ID, exercise.getUtteranceId());
        args.putString(ARG_CORRECT_ROLE_ID, exercise.getCorrectRoleId());
        args.putStringArrayList(ARG_OPTIONS, new ArrayList<>(exercise.getTranslationOptions()));
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
        return inflater.inflate(R.layout.fragment_exercise_type_c, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args == null) return;

        int index = args.getInt(ARG_INDEX);
        int total = args.getInt(ARG_TOTAL);

        view.<ImageView>findViewById(R.id.btnBack).setOnClickListener(v -> requireActivity().finish());
        view.<TextView>findViewById(R.id.tvCounter).setText((index + 1) + "/" + total);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setMax(total);
        progressBar.setProgress(index + 1);

        TextView tvInstruction = view.findViewById(R.id.tvInstruction);
        tvInstruction.setText("Укажите роль и переведите предложение");
        tvInstruction.setBackground(makePill(ACCENT_COLOR, 24));

        view.findViewById(R.id.btnSpeaker).setOnClickListener(v -> {
            // TODO: воспроизвести аудио для args.getString(ARG_UTTERANCE_ID)
        });

        List<Role> roles = MockDataProvider.getRoles();
        btnRoleR1 = view.findViewById(R.id.btnRoleR1);
        btnRoleR2 = view.findViewById(R.id.btnRoleR2);
        circleR1 = view.findViewById(R.id.circleR1);
        circleR2 = view.findViewById(R.id.circleR2);
        tvRoleR1 = view.findViewById(R.id.tvRoleR1);
        tvRoleR2 = view.findViewById(R.id.tvRoleR2);

        if (roles.size() >= 2) {
            roleIdFirst = roles.get(0).getId();
            roleIdSecond = roles.get(1).getId();
            tvRoleR1.setText(roles.get(0).getName());
            tvRoleR2.setText(roles.get(1).getName());
            btnRoleR1.setOnClickListener(v -> selectRole(roleIdFirst));
            btnRoleR2.setOnClickListener(v -> selectRole(roleIdSecond));
        }

        List<String> options = args.getStringArrayList(ARG_OPTIONS);
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
                btn.setOnClickListener(v -> selectTranslationOption(idx));
            } else {
                btn.setVisibility(View.GONE);
            }
        }
        btnSubmit.setOnClickListener(v -> { if (listener != null) listener.onAnswerSubmitted(); });
    }

    private void selectRole(String roleId) {
        selectedRoleId = roleId;
        boolean firstSelected = roleId.equals(roleIdFirst);

        btnRoleR1.setBackground(makePill(firstSelected ? ACCENT_COLOR : 0xFFFFFFFF, 14));
        circleR1.setBackgroundResource(firstSelected ? R.drawable.circle_filled : R.drawable.circle_outline);
        tvRoleR1.setTextColor(firstSelected ? 0xFFFFFFFF : 0xFF1A1A1A);

        btnRoleR2.setBackground(makePill(!firstSelected ? ACCENT_COLOR : 0xFFFFFFFF, 14));
        circleR2.setBackgroundResource(!firstSelected ? R.drawable.circle_filled : R.drawable.circle_outline);
        tvRoleR2.setTextColor(!firstSelected ? 0xFFFFFFFF : 0xFF1A1A1A);

        updateSubmitVisibility();
    }

    private void selectTranslationOption(int index) {
        selectedOptionIndex = index;
        for (int i = 0; i < optionButtons.length; i++) {
            if (i == index) {
                optionButtons[i].setBackground(makePill(ACCENT_COLOR, 14));
                optionButtons[i].setTextColor(0xFFFFFFFF);
            } else {
                optionButtons[i].setBackground(makePill(0xFFFFFFFF, 14));
                optionButtons[i].setTextColor(0xFF1A1A1A);
            }
        }
        updateSubmitVisibility();
    }

    private void updateSubmitVisibility() {
        if (selectedRoleId != null && selectedOptionIndex >= 0) {
            btnSubmit.setVisibility(View.VISIBLE);
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