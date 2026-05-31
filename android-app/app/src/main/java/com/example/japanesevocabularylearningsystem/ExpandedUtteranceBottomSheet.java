package com.example.japanesevocabularylearningsystem;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.japanesevocabularylearningsystem.model.Utterance;

import java.util.List;

public class ExpandedUtteranceBottomSheet extends DialogFragment {

    private static final String ARG_UTTERANCE = "utterance";

    public static ExpandedUtteranceBottomSheet newInstance(Utterance utterance) {
        ExpandedUtteranceBottomSheet fragment = new ExpandedUtteranceBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_UTTERANCE, utterance);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expanded_utterance, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9f);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Utterance utterance = (Utterance) requireArguments().getSerializable(ARG_UTTERANCE);
        if (utterance == null) return;

        TextView tvType       = view.findViewById(R.id.tvUtteranceType);
        TextView tvRomaji     = view.findViewById(R.id.tvRomaji);
        TextView tvTranslation= view.findViewById(R.id.tvTranslation);
        LinearLayout llSteps  = view.findViewById(R.id.llSteps);
        TextView tvRoleValue  = view.findViewById(R.id.tvRoleValue);
        View intentRow        = view.findViewById(R.id.intentRow);
        TextView tvIntentValue= view.findViewById(R.id.tvIntentValue);
        ImageButton btnClose  = view.findViewById(R.id.btnClose);
        ImageButton btnPlayAudio = view.findViewById(R.id.btnPlayAudio);

        tvType.setText(typeToRussian(utterance.getType()));
        tvRomaji.setText(utterance.getSurfaceRomaji() != null ? utterance.getSurfaceRomaji() : "");
        tvTranslation.setText(utterance.getTranslation() != null ? utterance.getTranslation() : "");

        // Шаги
        List<String> steps = utterance.getStepDisplayNames();
        if (steps != null && !steps.isEmpty()) {
            llSteps.setVisibility(View.VISIBLE);
            for (String step : steps) {
                llSteps.addView(makeTagView(step));
            }
        } else {
            llSteps.setVisibility(View.GONE);
        }

        // Роль
        tvRoleValue.setText(joinOrDash(utterance.getRoleDisplayNames()));

        // Намерение — скрыть для типа "Слово"
        if ("LEXICAL_UNIT".equals(utterance.getType())) {
            intentRow.setVisibility(View.GONE);
        } else {
            tvIntentValue.setText(joinOrDash(utterance.getCommunicativeIntentNames()));
        }

        btnClose.setOnClickListener(v -> dismiss());
        btnPlayAudio.setOnClickListener(v -> { /* TODO: аудио */ });
    }

    private TextView makeTagView(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#4A4A4A"));
        tv.setTextSize(12f);
        tv.setIncludeFontPadding(false);
        tv.setGravity(Gravity.CENTER);
        tv.setBackground(requireContext().getDrawable(R.drawable.bg_utterance_type));

        int ph = dpToPx(10);
        int pv = dpToPx(0);
        tv.setPadding(ph, pv, ph, pv);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(20));
        lp.bottomMargin = dpToPx(4);
        tv.setLayoutParams(lp);
        return tv;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private String typeToRussian(String type) {
        if (type == null) return "";
        switch (type) {
            case "UTTERANCE":    return "Активное выражение";
            case "TEMPLATE":     return "Шаблон";
            case "LEXICAL_UNIT": return "Слово";
            default:             return type;
        }
    }

    private String joinOrDash(List<String> list) {
        if (list == null || list.isEmpty()) return "—";
        return String.join(", ", list);
    }
}