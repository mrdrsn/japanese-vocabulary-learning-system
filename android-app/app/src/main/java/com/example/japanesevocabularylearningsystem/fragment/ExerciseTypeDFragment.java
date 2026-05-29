package com.example.japanesevocabularylearningsystem.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.japanesevocabularylearningsystem.R;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeD;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTypeDFragment extends Fragment {

    private static final int ACCENT_COLOR = 0xFF70C8B9;

    private static final String ARG_PROMPT_ID = "promptId";
    private static final String ARG_SAID_OPTIONS = "saidOptions";
    private static final String ARG_RESP_OPTIONS = "respOptions";
    private static final String ARG_INDEX = "index";
    private static final String ARG_TOTAL = "total";

    private OnAnswerSubmittedListener listener;
    private AppCompatButton btnSaid1, btnSaid2, btnResp1, btnResp2, btnSubmit;
    private int selectedSaidIndex = -1;
    private int selectedRespIndex = -1;

    public static ExerciseTypeDFragment newInstance(ExerciseTypeD exercise, int index, int total) {
        ExerciseTypeDFragment fragment = new ExerciseTypeDFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROMPT_ID, exercise.getPromptUtteranceId());
        args.putStringArrayList(ARG_SAID_OPTIONS, new ArrayList<>(exercise.getSaidOptions()));
        args.putStringArrayList(ARG_RESP_OPTIONS, new ArrayList<>(exercise.getResponseOptions()));
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
        return inflater.inflate(R.layout.fragment_exercise_type_d, container, false);
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
        tvInstruction.setText("Выберите подходящие ответы");
        tvInstruction.setBackground(makePill(ACCENT_COLOR, 24));

        view.findViewById(R.id.btnSpeaker).setOnClickListener(v -> {
            // TODO: воспроизвести аудио для args.getString(ARG_PROMPT_ID)
        });

        List<String> saidOptions = args.getStringArrayList(ARG_SAID_OPTIONS);
        List<String> respOptions = args.getStringArrayList(ARG_RESP_OPTIONS);

        btnSaid1 = view.findViewById(R.id.btnSaid1);
        btnSaid2 = view.findViewById(R.id.btnSaid2);
        btnResp1 = view.findViewById(R.id.btnResp1);
        btnResp2 = view.findViewById(R.id.btnResp2);
        btnSubmit = view.findViewById(R.id.btnSubmitAnswer);

        if (saidOptions != null && saidOptions.size() >= 2) {
            btnSaid1.setText(saidOptions.get(0));
            btnSaid2.setText(saidOptions.get(1));
        }
        if (respOptions != null && respOptions.size() >= 2) {
            btnResp1.setText(respOptions.get(0));
            btnResp2.setText(respOptions.get(1));
        }

        btnSaid1.setOnClickListener(v -> selectSaid(0));
        btnSaid2.setOnClickListener(v -> selectSaid(1));
        btnResp1.setOnClickListener(v -> selectResp(0));
        btnResp2.setOnClickListener(v -> selectResp(1));

        btnSubmit.setOnClickListener(v -> { if (listener != null) listener.onAnswerSubmitted(); });
    }

    private void selectSaid(int index) {
        selectedSaidIndex = index;
        if (index == 0) {
            btnSaid1.setBackground(makePill(ACCENT_COLOR, 14)); btnSaid1.setTextColor(0xFFFFFFFF);
            btnSaid2.setBackground(makePill(0xFFFFFFFF, 14));   btnSaid2.setTextColor(0xFF1A1A1A);
        } else {
            btnSaid2.setBackground(makePill(ACCENT_COLOR, 14)); btnSaid2.setTextColor(0xFFFFFFFF);
            btnSaid1.setBackground(makePill(0xFFFFFFFF, 14));   btnSaid1.setTextColor(0xFF1A1A1A);
        }
        updateSubmitVisibility();
    }

    private void selectResp(int index) {
        selectedRespIndex = index;
        if (index == 0) {
            btnResp1.setBackground(makePill(ACCENT_COLOR, 14)); btnResp1.setTextColor(0xFFFFFFFF);
            btnResp2.setBackground(makePill(0xFFFFFFFF, 14));   btnResp2.setTextColor(0xFF1A1A1A);
        } else {
            btnResp2.setBackground(makePill(ACCENT_COLOR, 14)); btnResp2.setTextColor(0xFFFFFFFF);
            btnResp1.setBackground(makePill(0xFFFFFFFF, 14));   btnResp1.setTextColor(0xFF1A1A1A);
        }
        updateSubmitVisibility();
    }

    private void updateSubmitVisibility() {
        if (selectedSaidIndex >= 0 && selectedRespIndex >= 0) {
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