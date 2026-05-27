package com.example.japanesevocabularylearningsystem;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int spaceDp;

    public SpaceItemDecoration(int spaceDp) {
        this.spaceDp = spaceDp;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        float density = view.getResources().getDisplayMetrics().density;
        int spacePx = Math.round(spaceDp * density);
        // Отступ только снизу каждого элемента кроме последнего
        int position = parent.getChildAdapterPosition(view);
        int total = parent.getAdapter() != null ? parent.getAdapter().getItemCount() : 0;
        if (position < total - 1) {
            outRect.bottom = spacePx;
        }
    }
}