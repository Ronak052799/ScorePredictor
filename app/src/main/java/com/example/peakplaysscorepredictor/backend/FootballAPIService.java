package com.example.peakplaysscorepredictor.backend;

// 1. API Service Interface
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface FootballAPIService {
    @GET("teams")
    Call<APIPremierLeagueTeams> getTeams(
            @Query("league") int league,
            @Query("season") int season,
            @Header("X-RapidAPI-Key") String rapidApiKey
    );

    @GET("players")
    Call<APIResponseWrapper.apiWrapper> getPlayerStats(
            @Query("team") int teamId,
            @Query("season") int season,
            @Query("league") int league,
            @Header("x-apisports-key") String apiSportsKey,
            @Query("page") int page
    );
}