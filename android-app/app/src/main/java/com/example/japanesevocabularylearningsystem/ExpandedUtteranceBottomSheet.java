package com.example.japanesevocabularylearningsystem;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.japanesevocabularylearningsystem.model.Utterance;
import com.example.japanesevocabularylearningsystem.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class ExpandedUtteranceBottomSheet extends DialogFragment {

    private static final String ARG_UTTERANCE = "utterance";

    private Utterance templateUtterance;
    private boolean isLuMode = false;
    private boolean isExamplesShowing = false;

    private TextView tvType;
    private TextView tvRomaji;
    private TextView tvTranslation;
    private LinearLayout llSteps;
    private TextView tvRoleValue;
    private View intentRow;
    private TextView tvIntentValue;
    private ImageButton btnClose;
    private ImageButton btnPlayAudio;
    private ImageButton btnBackToTemplate;
    private TextView chipExample;
    private View exampleRow;
    private String currentAudioUrl;

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
            dialog.getWindow().setGravity(
                    isExamplesShowing ? Gravity.TOP | Gravity.CENTER_HORIZONTAL : Gravity.CENTER);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (isExamplesShowing) {
                WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
                p.y = dpToPx(40);
                dialog.getWindow().setAttributes(p);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        templateUtterance = (Utterance) requireArguments().getSerializable(ARG_UTTERANCE);
        if (templateUtterance == null) return;

        tvType            = view.findViewById(R.id.tvUtteranceType);
        tvRomaji          = view.findViewById(R.id.tvRomaji);
        tvTranslation     = view.findViewById(R.id.tvTranslation);
        llSteps           = view.findViewById(R.id.llSteps);
        tvRoleValue       = view.findViewById(R.id.tvRoleValue);
        intentRow         = view.findViewById(R.id.intentRow);
        tvIntentValue     = view.findViewById(R.id.tvIntentValue);
        btnClose          = view.findViewById(R.id.btnClose);
        btnPlayAudio      = view.findViewById(R.id.btnPlayAudio);
        btnBackToTemplate = view.findViewById(R.id.btnBackToTemplate);
        chipExample       = view.findViewById(R.id.chipExample);
        exampleRow        = view.findViewById(R.id.exampleRow);

        showTemplateContent(templateUtterance);

        btnClose.setOnClickListener(v -> dismiss());
        btnPlayAudio.setOnClickListener(v ->
                AudioPlayer.play(ApiClient.fullAudioUrl(currentAudioUrl)));

        // Назад: вернуть шаблон и переоткрыть шит с примерами
        btnBackToTemplate.setOnClickListener(v -> {
            isLuMode = false;
            showTemplateContent(templateUtterance);
            openExamples();
        });

        if (templateUtterance.getExamples() != null
                && !templateUtterance.getExamples().isEmpty()) {
            chipExample.setOnClickListener(v -> openExamples());
        }

        // Пользователь выбрал ЛЕ → шит уже сворачивается, карточка идёт в центр
        getParentFragmentManager().setFragmentResultListener(
                ExamplesBottomSheet.RESULT_LU_CLICKED, getViewLifecycleOwner(),
                (key, result) -> {
                    isLuMode = true;          // до прихода RESULT_SHEET_DISMISSED
                    isExamplesShowing = false;
                    String luId     = result.getString(ExamplesBottomSheet.KEY_LU_ID);
                    String luRomaji = result.getString(ExamplesBottomSheet.KEY_LU_ROMAJI);
                    returnToCenter();
                    enterLuMode(luId, luRomaji);
                });

        // Шит закрыт пользователем вручную (не кликом по ЛЕ)
        getParentFragmentManager().setFragmentResultListener(
                ExamplesBottomSheet.RESULT_SHEET_DISMISSED, getViewLifecycleOwner(),
                (key, result) -> {
                    isExamplesShowing = false;
                    if (!isLuMode) returnToCenter();
                });
    }

    // ── Контент шаблона ───────────────────────────────────────────────────────

    private void showTemplateContent(Utterance utterance) {
        tvType.setText(typeToRussian(utterance.getType()));
        tvRomaji.setText(utterance.getSurfaceRomaji() != null ? utterance.getSurfaceRomaji() : "");
        currentAudioUrl = utterance.getAudioUrl();
        tvTranslation.setText(utterance.getTranslation() != null ? utterance.getTranslation() : "");

        refreshSteps(utterance.getStepDisplayNames());
        tvRoleValue.setText(joinOrDash(utterance.getRoleDisplayNames()));

        if ("LEXICAL_UNIT".equals(utterance.getType())) {
            intentRow.setVisibility(View.GONE);
        } else {
            intentRow.setVisibility(View.VISIBLE);
            tvIntentValue.setText(joinOrDash(utterance.getCommunicativeIntentNames()));
        }

        btnBackToTemplate.setVisibility(View.GONE);

        boolean hasExamples = utterance.getExamples() != null
                && !utterance.getExamples().isEmpty();
        if (exampleRow != null)
            exampleRow.setVisibility(hasExamples ? View.VISIBLE : View.GONE);
    }

    // ── Режим ЛЕ ─────────────────────────────────────────────────────────────

    private void enterLuMode(String luId, String luRomaji) {
        Utterance lu = findLu(luId);
        currentAudioUrl = lu != null ? lu.getAudioUrl() : null;

        tvType.setText(typeToRussian("LEXICAL_UNIT"));
        tvRomaji.setText(luRomaji != null ? luRomaji : "");
        tvTranslation.setText(lu != null && lu.getTranslation() != null
                ? lu.getTranslation() : "");

        tvRoleValue.setText(joinOrDash(lu != null ? lu.getRoleDisplayNames() : null));
        intentRow.setVisibility(View.GONE);
        refreshSteps(lu != null ? lu.getStepDisplayNames() : null);

        btnBackToTemplate.setVisibility(View.VISIBLE);
        if (exampleRow != null) exampleRow.setVisibility(View.GONE);
    }

    private Utterance findLu(String luId) {
        for (Utterance u : LexicalViewActivity.allUtterances) {
            if (luId != null && luId.equals(u.getId())) return u;
        }
        return null;
    }

    // ── Примеры ───────────────────────────────────────────────────────────────

    private void openExamples() {
        isExamplesShowing = true;
        moveToTop();
        ArrayList<Utterance.ExampleEntry> examples =
                new ArrayList<>(templateUtterance.getExamples());
        ExamplesBottomSheet sheet = ExamplesBottomSheet.newInstance(
                templateUtterance.getSurfaceRomaji(), examples);
        sheet.show(requireActivity().getSupportFragmentManager(), "examples");
    }

    // ── Позиционирование ──────────────────────────────────────────────────────

    private void moveToTop() {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9f);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
            p.y = dpToPx(40);
            dialog.getWindow().setAttributes(p);
        }
    }

    private void returnToCenter() {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9f);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
            WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
            p.y = 0;
            dialog.getWindow().setAttributes(p);
        }
    }

    // ── Вспомогательные ───────────────────────────────────────────────────────

    private void refreshSteps(List<String> steps) {
        llSteps.removeAllViews();
        if (steps != null && !steps.isEmpty()) {
            llSteps.setVisibility(View.VISIBLE);
            for (String step : steps) llSteps.addView(makeTagView(step));
        } else {
            llSteps.setVisibility(View.GONE);
        }
    }

    private TextView makeTagView(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#4A4A4A"));
        tv.setTextSize(12f);
        tv.setIncludeFontPadding(false);
        tv.setGravity(Gravity.CENTER);
        tv.setBackground(requireContext().getDrawable(R.drawable.bg_step_tag_purple));
        int ph = dpToPx(10);
        tv.setPadding(ph, 0, ph, 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(20));
        lp.bottomMargin = dpToPx(4);
        tv.setLayoutParams(lp);
        return tv;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
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