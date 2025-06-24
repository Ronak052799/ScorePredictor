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

    @GET("fixtures")
    Call<FixtureResponseWrapper.ApiResponse> getRecentFixtures(
            @Query("team") int teamId,
            @Query("last") int lastCount,
            @Query("season") int season,
            @Query("league") int leagueId, // usually 39 for Premier League
            @Header("x-apisports-key") String apiSportsKey

            );
    @GET("fixtures/headtohead")
    Call<FixtureResponseWrapper.ApiResponse> getHeadToHeadFixtures(
            @Query("h2h") String h2h,
            @Query("last") int last,
            @Header("x-apisports-key") String apiKey
    );


}