package com.example.japanesevocabularylearningsystem.network;

import com.example.japanesevocabularylearningsystem.network.dto.ExerciseTypeADto;
import com.example.japanesevocabularylearningsystem.network.dto.ExerciseTypeBDto;
import com.example.japanesevocabularylearningsystem.network.dto.FullLexiconDto;
import com.example.japanesevocabularylearningsystem.network.dto.ScenarioDto;
import com.example.japanesevocabularylearningsystem.network.dto.UtteranceDto;
import com.example.japanesevocabularylearningsystem.network.dto.ExerciseTypeCDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/scenarios")
    Call<List<ScenarioDto>> getScenarios();

    @GET("api/scenarios/{id}")
    Call<ScenarioDto> getScenario(@Path("id") String id);

    @GET("api/scenarios/{id}/full-lexicon")
    Call<FullLexiconDto> getFullLexicon(@Path("id") String id);

    @GET("api/scenarios/{id}/exercises/type-a")
    Call<List<ExerciseTypeADto>> getTypeAExercises(
            @Path("id") String scenarioId,
            @Query("count") int count,
            @Query("roleId") String roleId);

    @GET("api/scenarios/{id}/exercises/type-b")
    Call<List<ExerciseTypeBDto>> getTypeBExercises(
            @Path("id") String scenarioId,
            @Query("count") int count,
            @Query("roleId") String roleId);

    @GET("api/scenarios/{id}/exercises/type-c")
    Call<List<ExerciseTypeCDto>> getTypeCExercises(
            @Path("id") String scenarioId,
            @Query("count") int count);
}