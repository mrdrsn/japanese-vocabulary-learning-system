package com.example.japanesevocabularylearningsystem;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.model.CommunicativeIntent;
import com.example.japanesevocabularylearningsystem.model.Role;
import com.example.japanesevocabularylearningsystem.model.Utterance;

public class ExpandedUtteranceBottomSheet extends DialogFragment {

    private static final String ARG_UTTERANCE_ID = "utterance_id";

    public static ExpandedUtteranceBottomSheet newInstance(String utteranceId) {
        ExpandedUtteranceBottomSheet sheet = new ExpandedUtteranceBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_UTTERANCE_ID, utteranceId);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expanded_utterance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String utteranceId = getArguments() != null
                ? getArguments().getString(ARG_UTTERANCE_ID)
                : null;

        Utterance utterance = MockDataProvider.getUtteranceById(utteranceId);
        if (utterance == null) return;

        Role role = MockDataProvider.getRoleById(utterance.getRoleId());
        CommunicativeIntent intent = MockDataProvider.getIntentById(utterance.getCommunicativeIntentId());

        TextView tvUtteranceType = view.findViewById(R.id.tvUtteranceType);
        TextView tvStepTag       = view.findViewById(R.id.tvStepTag);
        TextView tvRomaji        = view.findViewById(R.id.tvRomaji);
        TextView tvTranslation   = view.findViewById(R.id.tvTranslation);
        TextView tvRoleValue     = view.findViewById(R.id.tvRoleValue);
        TextView tvIntentValue   = view.findViewById(R.id.tvIntentValue);
        TextView tvExampleValue  = view.findViewById(R.id.tvExampleValue);
        ImageButton btnCollapse  = view.findViewById(R.id.btnCollapse);
        ImageButton btnPlayAudio = view.findViewById(R.id.btnPlayAudio);

        tvUtteranceType.setText(utterance.isFixedExpression() ? "Выражение" : "Шаблон");
        tvStepTag.setText(utterance.getStepName() != null ? utterance.getStepName() : "");
        tvRomaji.setText(utterance.getSurfaceRomaji());
        tvTranslation.setText(utterance.getTranslation());
        tvRoleValue.setText(role != null ? role.getName() : "—");
        tvIntentValue.setText(intent != null ? intent.getName() : "—");
        tvExampleValue.setText("");

        btnCollapse.setOnClickListener(v -> dismiss());

        btnPlayAudio.setOnClickListener(v -> {
            // TODO: воспроизвести аудио
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            // прозрачный фон — чтобы была видна только карточка
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            // карточка растягивается на всю ширину с отступами из layout
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            // центрирование на экране
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }
}