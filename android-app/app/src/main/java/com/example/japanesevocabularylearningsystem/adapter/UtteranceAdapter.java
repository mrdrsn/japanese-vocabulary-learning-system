package com.example.japanesevocabularylearningsystem.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japanesevocabularylearningsystem.ExpandedUtteranceBottomSheet;
import com.example.japanesevocabularylearningsystem.R;
import com.example.japanesevocabularylearningsystem.model.Utterance;

import java.util.List;

public class UtteranceAdapter extends RecyclerView.Adapter<UtteranceAdapter.UtteranceViewHolder> {

    private List<Utterance> utteranceList;
    private final FragmentManager fragmentManager;

    public UtteranceAdapter(List<Utterance> utteranceList, FragmentManager fragmentManager) {
        this.utteranceList = utteranceList;
        this.fragmentManager = fragmentManager;
    }

    public void updateData(List<Utterance> newList) {
        utteranceList.clear();
        utteranceList.addAll(newList);
        notifyDataSetChanged();
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

        holder.btnExpandUtterance.setOnClickListener(v -> {
            ExpandedUtteranceBottomSheet sheet =
                    ExpandedUtteranceBottomSheet.newInstance(utterance.getId());
            sheet.show(fragmentManager, "expanded_utterance");
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

        TextView tvRomaji;
        TextView tvTranslation;
        ImageButton btnExpandUtterance;
        ImageButton btnPlayAudio;

        public UtteranceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRomaji           = itemView.findViewById(R.id.tvRomaji);
            tvTranslation      = itemView.findViewById(R.id.tvTranslation);
            btnExpandUtterance = itemView.findViewById(R.id.btnExpandUtterance);
            btnPlayAudio       = itemView.findViewById(R.id.btnPlayAudio);
        }
    }
}