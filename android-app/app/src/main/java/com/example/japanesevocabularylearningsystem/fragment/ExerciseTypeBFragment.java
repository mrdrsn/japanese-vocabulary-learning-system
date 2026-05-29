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
import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.model.ExerciseTypeB;
import com.example.japanesevocabularylearningsystem.model.Role;

import java.util.ArrayList;
import java.util.List;

public class ExerciseTypeBFragment extends Fragment {

    private static final int ACCENT_COLOR = 0xFF8EC3FF;

    private static final String ARG_UTTERANCE_ID = "utteranceId";
    private static final String ARG_ROLE_ID = "roleId";
    private static final String ARG_OPTIONS = "options";
    private static final String ARG_INDEX = "index";
    private static final String ARG_TOTAL = "total";

    private OnAnswerSubmittedListener listener;
    private AppCompatButton[] optionButtons;
    private AppCompatButton btnSubmit;

    public static ExerciseTypeBFragment newInstance(ExerciseTypeB exercise, int index, int total) {
        ExerciseTypeBFragment fragment = new ExerciseTypeBFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UTTERANCE_ID, exercise.getUtteranceId());
        args.putString(ARG_ROLE_ID, exercise.getRoleId());
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
        return inflater.inflate(R.layout.fragment_exercise_type_b, container, false);
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
        tvInstruction.setText("Прослушайте и переведите фразу");
        tvInstruction.setBackground(makePill(ACCENT_COLOR, 24));

        view.findViewById(R.id.btnSpeaker).setOnClickListener(v -> {
            // TODO: воспроизвести аудио для args.getString(ARG_UTTERANCE_ID)
        });

        Role role = MockDataProvider.getRoleById(args.getString(ARG_ROLE_ID));
        view.<TextView>findViewById(R.id.tvRoleValue).setText(role != null ? role.getName() : "");

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
                btn.setOnClickListener(v -> selectOption(idx));
            } else {
                btn.setVisibility(View.GONE);
            }
        }
        btnSubmit.setOnClickListener(v -> { if (listener != null) listener.onAnswerSubmitted(); });
    }

    private void selectOption(int index) {
        for (int i = 0; i < optionButtons.length; i++) {
            if (i == index) {
                optionButtons[i].setBackground(makePill(ACCENT_COLOR, 14));
                optionButtons[i].setTextColor(0xFFFFFFFF);
            } else {
                optionButtons[i].setBackground(makePill(0xFFFFFFFF, 14));
                optionButtons[i].setTextColor(0xFF1A1A1A);
            }
        }
        btnSubmit.setVisibility(View.VISIBLE);
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