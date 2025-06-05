package com.example.peakplaysscorepredictor.backend;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FootballRepository {
    private FootballAPIService apiService;
    private static final String TAG = "FootballRepository";

    public FootballRepository() {
        apiService = APIClient.getAPIService();
    }

    public interface DataCallback {
        void onSuccess(Map<String, List<APIResponseWrapper.PlayerWrapper>> teamPlayerMap);
        void onError(String error);
    }

    public void fetchTeamsAndPlayerStats(int season, DataCallback callback) {
        // First API call - get all Premier League teams
        Call<APIPremierLeagueTeams> teamsCall = apiService.getTeams(39, season, APIClient.getApiKey());

        teamsCall.enqueue(new Callback<APIPremierLeagueTeams>() {
            @Override
            public void onResponse(Call<APIPremierLeagueTeams> call, Response<APIPremierLeagueTeams> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<APIPremierLeagueTeams.TeamData> teams = response.body().getResponse();
                    fetchPlayerStatsForAllTeams(teams, season, callback);
                } else {
                    callback.onError("Failed to fetch teams: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<APIPremierLeagueTeams> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to fetch teams", t);
            }
        });
    }

    private void fetchPlayerStatsForAllTeams(List<APIPremierLeagueTeams.TeamData> teams,
                                             int season, DataCallback callback) {
        Map<String, List<APIResponseWrapper.PlayerWrapper>> teamPlayerMap = new HashMap<>();
        int[] completedCalls = {0}; // Array to make it effectively final for lambda

        for (APIPremierLeagueTeams.TeamData teamData : teams) {
            int teamId = teamData.getTeam().getId();
            String teamName = teamData.getTeam().getName();

            Call<APIResponseWrapper.apiWrapper> playerCall =
                    apiService.getPlayerStats(teamId, season, 39, APIClient.getApiKey());

            playerCall.enqueue(new Callback<APIResponseWrapper.apiWrapper>() {
                @Override
                public void onResponse(Call<APIResponseWrapper.apiWrapper> call,
                                       Response<APIResponseWrapper.apiWrapper> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<APIResponseWrapper.PlayerWrapper> players = response.body().getResponse();
                        if (players != null) {
                            teamPlayerMap.put(teamName, players);
                            Log.d(TAG, "Successfully fetched " + players.size() + " players for " + teamName);
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch players for " + teamName + ": " + response.message());
                    }

                    // Check if all calls are completed
                    completedCalls[0]++;
                    if (completedCalls[0] == teams.size()) {
                        callback.onSuccess(teamPlayerMap);
                    }
                }

                @Override
                public void onFailure(Call<APIResponseWrapper.apiWrapper> call, Throwable t) {
                    Log.e(TAG, "Failed to fetch players for " + teamName, t);

                    // Still count this as completed to avoid hanging
                    completedCalls[0]++;
                    if (completedCalls[0] == teams.size()) {
                        callback.onSuccess(teamPlayerMap);
                    }
                }
            });
        }
    }

}
