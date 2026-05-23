package com.example.japanesevocabularylearningsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japanesevocabularylearningsystem.R;
import com.example.japanesevocabularylearningsystem.model.Utterance;

import java.util.List;

public class UtteranceAdapter extends RecyclerView.Adapter<UtteranceAdapter.UtteranceViewHolder> {

    private final List<Utterance> utteranceList;

    public UtteranceAdapter(List<Utterance> utteranceList) {
        this.utteranceList = utteranceList;
    }

    @NonNull
    @Override
    public UtteranceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_utterance, parent, false);
        return new UtteranceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UtteranceViewHolder holder, int position) {
        Utterance utterance = utteranceList.get(position);

        holder.tvRomaji.setText(utterance.getSurfaceRomaji());
        holder.tvTranslation.setText(utterance.getTranslation());

        if (utterance.isFixedExpression()) {
            holder.tvUtteranceType.setText("Выражение");
        } else {
            holder.tvUtteranceType.setText("Шаблон");
        }

        holder.btnExpandUtterance.setOnClickListener(v -> {
            // TODO: открыть расширенную карточку
        });

        holder.btnPlayAudio.setOnClickListener(v -> {
            // TODO: воспроизвести аудио
        });
    }

    @Override
    public int getItemCount() {
        return utteranceList.size();
    }

    static class UtteranceViewHolder extends RecyclerView.ViewHolder {

        TextView tvUtteranceType;
        TextView tvRomaji;
        TextView tvTranslation;
        ImageButton btnExpandUtterance;
        ImageButton btnPlayAudio;

        public UtteranceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUtteranceType = itemView.findViewById(R.id.tvUtteranceType);
            tvRomaji = itemView.findViewById(R.id.tvRomaji);
            tvTranslation = itemView.findViewById(R.id.tvTranslation);
            btnExpandUtterance = itemView.findViewById(R.id.btnExpandUtterance);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);
        }
    }
}