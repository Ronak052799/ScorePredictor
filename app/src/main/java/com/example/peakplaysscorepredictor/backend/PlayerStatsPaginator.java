package com.example.peakplaysscorepredictor.backend;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatsPaginator {

    public interface PlayerStatsCallback {
        void onComplete(List<APIResponseWrapper.PlayerWrapper> allPlayers);
        void onError(String error);
    }

    public static void fetchAllPages(FootballAPIService apiService, int teamId, int season, int leagueId, String apiKey, PlayerStatsCallback callback) {
        List<APIResponseWrapper.PlayerWrapper> allPlayers = new ArrayList<>();
        fetchPage(apiService, teamId, season, leagueId, apiKey, 1, allPlayers, callback);
    }

    private static void fetchPage(FootballAPIService apiService, int teamId, int season, int leagueId, String apiKey,
                                  int page, List<APIResponseWrapper.PlayerWrapper> accumulatedPlayers, PlayerStatsCallback callback) {

        Call<APIResponseWrapper.apiWrapper> call = apiService.getPlayerStats(teamId, season, leagueId, apiKey, page);

        call.enqueue(new Callback<APIResponseWrapper.apiWrapper>() {
            @Override
            public void onResponse(Call<APIResponseWrapper.apiWrapper> call, Response<APIResponseWrapper.apiWrapper> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<APIResponseWrapper.PlayerWrapper> players = response.body().getResponse();
                    if (players != null && !players.isEmpty()) {
                        accumulatedPlayers.addAll(players);
                        // Continue to next page
                        fetchPage(apiService, teamId, season, leagueId, apiKey, page + 1, accumulatedPlayers, callback);
                    } else {
                        // No more pages
                        callback.onComplete(accumulatedPlayers);
                    }
                } else {
                    callback.onError("Error fetching player stats page " + page + ": " + response.message());
                }
            }

            @Override
            public void onFailure(Call<APIResponseWrapper.apiWrapper> call, Throwable t) {
                callback.onError("Failure fetching player stats page " + page + ": " + t.getMessage());
            }
        });
    }
}

