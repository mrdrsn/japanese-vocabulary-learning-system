package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japanesevocabularylearningsystem.adapter.UtteranceAdapter;
import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.model.Utterance;
import com.example.japanesevocabularylearningsystem.network.ApiClient;
import com.example.japanesevocabularylearningsystem.network.dto.FullLexiconDto;
import com.example.japanesevocabularylearningsystem.network.dto.LexicalItemDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LexicalViewActivity extends AppCompatActivity {

    // Доступен из ExpandedUtteranceBottomSheet для поиска ЛЕ
    public static final List<Utterance> allUtterances = new ArrayList<>();

    private UtteranceAdapter adapter;
    private final List<Utterance> utteranceList = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lexical_view);

        ImageView btnBack = findViewById(R.id.btnBack);
        RecyclerView rvUtterances = findViewById(R.id.rvUtterances);
        progressBar = findViewById(R.id.progressBar);

        adapter = new UtteranceAdapter(utteranceList, getSupportFragmentManager());
        rvUtterances.setLayoutManager(new LinearLayoutManager(this));
        rvUtterances.setAdapter(adapter);

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, ModeChooseActivity.class));
            finish();
        });

        loadLexicon();
    }

    private void loadLexicon() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        ApiClient.getInstance().getFullLexicon("SC1")
                .enqueue(new Callback<FullLexiconDto>() {
                    @Override
                    public void onResponse(Call<FullLexiconDto> call, Response<FullLexiconDto> response) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().items != null) {
                            utteranceList.clear();
                            allUtterances.clear();
                            for (LexicalItemDto dto : response.body().items) {
                                utteranceList.add(toModel(dto));
                            }
                            allUtterances.addAll(utteranceList);
                            adapter.notifyDataSetChanged();
                        } else {
                            fallbackToMock();
                        }
                    }

                    @Override
                    public void onFailure(Call<FullLexiconDto> call, Throwable t) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        fallbackToMock();
                    }
                });
    }

    private Utterance toModel(LexicalItemDto dto) {
        Utterance u = new Utterance();
        u.setId(dto.id);
        u.setSurfaceRomaji(dto.romaji);
        u.setTranslation(dto.translation);
        u.setType(dto.type);
        u.setStepDisplayNames(dto.stepDisplayNames);
        u.setRoleDisplayNames(dto.roleDisplayNames);
        u.setCommunicativeIntentNames(dto.communicativeIntentNames);
        u.setAudioUrl(dto.audioUrl);
        if (dto.examples != null) {
            List<Utterance.ExampleEntry> entries = new ArrayList<>();
            for (LexicalItemDto.ExampleEntryDto e : dto.examples) {
                Utterance.ExampleEntry ex = new Utterance.ExampleEntry(
                        e.luId, e.luRomaji, e.filledRomaji, e.luTranslation);
                ex.luId2 = e.luId2;
                ex.luRomaji2 = e.luRomaji2;
                entries.add(ex);
            }
            u.setExamples(entries);
        }
        return u;
    }

    private void fallbackToMock() {
        utteranceList.clear();
        utteranceList.addAll(MockDataProvider.getConvenienceStoreUtterances());
        allUtterances.clear();
        allUtterances.addAll(utteranceList);
        adapter.notifyDataSetChanged();
    }
}