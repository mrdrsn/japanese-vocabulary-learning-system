package com.example.japanesevocabularylearningsystem.network;

import com.example.japanesevocabularylearningsystem.network.dto.ScenarioDto;
import com.example.japanesevocabularylearningsystem.network.dto.UtteranceDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/scenarios")
    Call<List<ScenarioDto>> getScenarios();

    @GET("api/scenarios/{id}")
    Call<ScenarioDto> getScenario(@Path("id") String id);

    @GET("api/scenarios/{id}/lexicon")
    Call<List<UtteranceDto>> getLexicon(@Path("id") String id);
}