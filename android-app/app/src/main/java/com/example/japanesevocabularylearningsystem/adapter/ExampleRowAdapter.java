package com.example.japanesevocabularylearningsystem.adapter;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japanesevocabularylearningsystem.R;
import com.example.japanesevocabularylearningsystem.model.Utterance;

import java.util.List;

public class ExampleRowAdapter extends RecyclerView.Adapter<ExampleRowAdapter.ViewHolder> {

    public interface OnLuClickListener {
        void onLuClick(String luId, String luRomaji);
    }

    private static final int LU_COLOR = Color.parseColor("#4A6CF7");

    private final List<Utterance.ExampleEntry> examples;
    private final OnLuClickListener listener;

    public ExampleRowAdapter(List<Utterance.ExampleEntry> examples, OnLuClickListener listener) {
        this.examples = examples;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_example_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Utterance.ExampleEntry entry = examples.get(position);

        holder.tvFilledRomaji.setText(buildSpannable(entry));
        holder.tvFilledRomaji.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvFilledRomaji.setHighlightColor(Color.TRANSPARENT);

        holder.tvExampleTranslation.setText(
                entry.luTranslation != null ? entry.luTranslation : "");
    }

    private SpannableStringBuilder buildSpannable(Utterance.ExampleEntry entry) {
        String full = entry.filledRomaji != null ? entry.filledRomaji : "";
        SpannableStringBuilder sb = new SpannableStringBuilder(full);

        // первый слот
        int end1 = applyLuSpan(sb, full, entry.luRomaji, entry.luId, 0);

        // второй слот — ищем начиная после первого, чтобы не перекрываться
        if (entry.luRomaji2 != null && !entry.luRomaji2.isEmpty()) {
            applyLuSpan(sb, full, entry.luRomaji2, entry.luId2, Math.max(0, end1));
        }

        return sb;
    }

    // Возвращает конец найденного span, или 0 если не найдено
    private int applyLuSpan(SpannableStringBuilder sb, String full,
                            String luRomaji, String luId, int searchFrom) {
        if (luRomaji == null || luRomaji.isEmpty()) return 0;
        int start = full.toLowerCase().indexOf(luRomaji.toLowerCase(), searchFrom);
        if (start < 0) return 0;
        int end = start + luRomaji.length();

        final String finalLuId = luId;
        final String finalLuRomaji = luRomaji;

        sb.setSpan(new ForegroundColorSpan(LU_COLOR),
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new UnderlineSpan(),
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (listener != null) listener.onLuClick(finalLuId, finalLuRomaji);
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(LU_COLOR);
                ds.setUnderlineText(true);
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return end;
    }

    @Override
    public int getItemCount() { return examples.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFilledRomaji;
        TextView tvExampleTranslation;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFilledRomaji = itemView.findViewById(R.id.tvFilledRomaji);
            tvExampleTranslation = itemView.findViewById(R.id.tvExampleTranslation);
        }
    }
}