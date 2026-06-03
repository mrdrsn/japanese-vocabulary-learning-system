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

import com.example.japanesevocabularylearningsystem.AudioPlayer;
import com.example.japanesevocabularylearningsystem.R;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeC;
import com.example.japanesevocabularylearningsystem.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTypeCFragment extends Fragment {

    private static final int COLOR_ACCENT  = 0xFF8EC3FF;
    private static final int COLOR_CORRECT = 0xFF4CAF50;
    private static final int COLOR_WRONG   = 0xFFF44336;
    private static final int COLOR_DEFAULT = 0xFFFFFFFF;

    private static final String ARG_INSTRUCTION   = "instruction";
    private static final String ARG_AUDIO_URL     = "audioUrl";
    private static final String ARG_ROLE_OPTIONS  = "roleOptions";
    private static final String ARG_ROLE_FLAGS    = "roleFlags";
    private static final String ARG_TRANS_OPTIONS = "transOptions";
    private static final String ARG_TRANS_FLAGS   = "transFlags";
    private static final String ARG_INDEX         = "index";
    private static final String ARG_TOTAL         = "total";

    private OnAnswerSubmittedListener listener;
    private AppCompatButton[] optionButtons;
    private AppCompatButton btnSubmit;
    private LinearLayout btnRoleR1, btnRoleR2;
    private View circleR1, circleR2;
    private TextView tvRoleR1, tvRoleR2;
    private boolean submitted = false;
    private int selectedRoleIndex = -1;
    private int selectedTranslationIndex = -1;

    public static ExerciseTypeCFragment newInstance(ExerciseTypeC exercise, int index, int total) {
        ExerciseTypeCFragment fragment = new ExerciseTypeCFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INSTRUCTION, exercise.getInstruction());
        args.putString(ARG_AUDIO_URL, exercise.getAudioUrl());
        args.putStringArrayList(ARG_ROLE_OPTIONS, new ArrayList<>(exercise.getRoleOptions()));

        List<Boolean> rFlags = exercise.getRoleCorrectFlags();
        boolean[] rArr = new boolean[rFlags != null ? rFlags.size() : 0];
        if (rFlags != null) {
            for (int i = 0; i < rFlags.size(); i++) rArr[i] = Boolean.TRUE.equals(rFlags.get(i));
        }
        args.putBooleanArray(ARG_ROLE_FLAGS, rArr);

        args.putStringArrayList(ARG_TRANS_OPTIONS, new ArrayList<>(exercise.getTranslationOptions()));

        List<Boolean> tFlags = exercise.getTranslationCorrectFlags();
        boolean[] tArr = new boolean[tFlags != null ? tFlags.size() : 0];
        if (tFlags != null) {
            for (int i = 0; i < tFlags.size(); i++) tArr[i] = Boolean.TRUE.equals(tFlags.get(i));
        }
        args.putBooleanArray(ARG_TRANS_FLAGS, tArr);

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
        tvInstruction.setText(args.getString(ARG_INSTRUCTION));
        tvInstruction.setBackground(makePill(COLOR_ACCENT, 24));

        String audioUrl = args.getString(ARG_AUDIO_URL);
        view.findViewById(R.id.btnSpeaker).setOnClickListener(v ->
                AudioPlayer.play(ApiClient.fullAudioUrl(audioUrl)));

        List<String> roleOptions = args.getStringArrayList(ARG_ROLE_OPTIONS);
        btnRoleR1 = view.findViewById(R.id.btnRoleR1);
        btnRoleR2 = view.findViewById(R.id.btnRoleR2);
        circleR1 = view.findViewById(R.id.circleR1);
        circleR2 = view.findViewById(R.id.circleR2);
        tvRoleR1 = view.findViewById(R.id.tvRoleR1);
        tvRoleR2 = view.findViewById(R.id.tvRoleR2);

        if (roleOptions != null && roleOptions.size() >= 1) {
            tvRoleR1.setText(roleOptions.get(0));
            btnRoleR1.setOnClickListener(v -> selectRole(0));
        }
        if (roleOptions != null && roleOptions.size() >= 2) {
            tvRoleR2.setText(roleOptions.get(1));
            btnRoleR2.setOnClickListener(v -> selectRole(1));
        }

        List<String> transOptions = args.getStringArrayList(ARG_TRANS_OPTIONS);
        btnSubmit = view.findViewById(R.id.btnSubmitAnswer);
        optionButtons = new AppCompatButton[]{
                view.findViewById(R.id.btnOption1),
                view.findViewById(R.id.btnOption2),
                view.findViewById(R.id.btnOption3),
                view.findViewById(R.id.btnOption4)
        };

        for (int i = 0; i < optionButtons.length; i++) {
            if (transOptions != null && i < transOptions.size()) {
                optionButtons[i].setText(transOptions.get(i));
                optionButtons[i].setVisibility(View.VISIBLE);
                final int idx = i;
                optionButtons[i].setOnClickListener(v -> selectTranslation(idx));
            } else {
                optionButtons[i].setVisibility(View.GONE);
            }
        }

        btnSubmit.setOnClickListener(v -> {
            if (!submitted) {
                submitted = true;
                showFeedback(args.getBooleanArray(ARG_ROLE_FLAGS),
                        args.getBooleanArray(ARG_TRANS_FLAGS));
                btnSubmit.setText("Продолжить");
            } else {
                if (listener != null) listener.onAnswerSubmitted();
            }
        });
    }

    private void selectRole(int index) {
        if (submitted) return;
        selectedRoleIndex = index;
        if (index == 0) {
            btnRoleR1.setBackground(makePill(COLOR_ACCENT, 14));
            circleR1.setBackgroundResource(R.drawable.circle_filled);
            tvRoleR1.setTextColor(0xFFFFFFFF);
            btnRoleR2.setBackground(makePill(COLOR_DEFAULT, 14));
            circleR2.setBackgroundResource(R.drawable.circle_outline);
            tvRoleR2.setTextColor(0xFF1A1A1A);
        } else {
            btnRoleR2.setBackground(makePill(COLOR_ACCENT, 14));
            circleR2.setBackgroundResource(R.drawable.circle_filled);
            tvRoleR2.setTextColor(0xFFFFFFFF);
            btnRoleR1.setBackground(makePill(COLOR_DEFAULT, 14));
            circleR1.setBackgroundResource(R.drawable.circle_outline);
            tvRoleR1.setTextColor(0xFF1A1A1A);
        }
        updateSubmitVisibility();
    }

    private void selectTranslation(int index) {
        if (submitted) return;
        selectedTranslationIndex = index;
        for (int i = 0; i < optionButtons.length; i++) {
            if (i == index) {
                optionButtons[i].setBackground(makePill(COLOR_ACCENT, 14));
                optionButtons[i].setTextColor(0xFFFFFFFF);
            } else {
                optionButtons[i].setBackground(makePill(COLOR_DEFAULT, 14));
                optionButtons[i].setTextColor(0xFF1A1A1A);
            }
        }
        updateSubmitVisibility();
    }

    private void updateSubmitVisibility() {
        btnSubmit.setVisibility(
                (selectedRoleIndex >= 0 && selectedTranslationIndex >= 0)
                        ? View.VISIBLE : View.GONE);
    }

    private void showFeedback(boolean[] roleFlags, boolean[] transFlags) {
        if (roleFlags != null) {
            if (roleFlags.length > 0)
                applyRoleFeedback(btnRoleR1, circleR1, tvRoleR1, roleFlags[0], selectedRoleIndex == 0);
            if (roleFlags.length > 1)
                applyRoleFeedback(btnRoleR2, circleR2, tvRoleR2, roleFlags[1], selectedRoleIndex == 1);
        }
        btnRoleR1.setClickable(false);
        btnRoleR2.setClickable(false);

        if (transFlags != null) {
            for (int i = 0; i < optionButtons.length; i++) {
                if (optionButtons[i].getVisibility() != View.VISIBLE) continue;
                boolean isCorrect = i < transFlags.length && transFlags[i];
                boolean isSelected = (i == selectedTranslationIndex);
                if (isCorrect) {
                    optionButtons[i].setBackground(makePill(COLOR_CORRECT, 14));
                    optionButtons[i].setTextColor(0xFFFFFFFF);
                } else if (isSelected) {
                    optionButtons[i].setBackground(makePill(COLOR_WRONG, 14));
                    optionButtons[i].setTextColor(0xFFFFFFFF);
                } else {
                    optionButtons[i].setBackground(makePill(COLOR_DEFAULT, 14));
                    optionButtons[i].setTextColor(0xFF1A1A1A);
                }
                optionButtons[i].setClickable(false);
            }
        }
    }

    private void applyRoleFeedback(LinearLayout btn, View circle, TextView tv,
                                   boolean isCorrect, boolean isSelected) {
        if (isCorrect) {
            btn.setBackground(makePill(COLOR_CORRECT, 14));
            circle.setBackgroundResource(R.drawable.circle_filled);
            tv.setTextColor(0xFFFFFFFF);
        } else if (isSelected) {
            btn.setBackground(makePill(COLOR_WRONG, 14));
            circle.setBackgroundResource(R.drawable.circle_filled);
            tv.setTextColor(0xFFFFFFFF);
        } else {
            btn.setBackground(makePill(COLOR_DEFAULT, 14));
            circle.setBackgroundResource(R.drawable.circle_outline);
            tv.setTextColor(0xFF1A1A1A);
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