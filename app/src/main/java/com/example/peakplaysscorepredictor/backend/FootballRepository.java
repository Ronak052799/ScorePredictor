package com.example.peakplaysscorepredictor.backend;

import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
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

    public void fetchPlayerStatsForAllTeams(List<APIPremierLeagueTeams.TeamData> teams,
                                            int season, DataCallback callback) {
        Map<String, List<APIResponseWrapper.PlayerWrapper>> teamPlayerMap = new HashMap<>();

        new Thread(() -> {
            int totalTeams = teams.size();
            int completed = 0;

            for (APIPremierLeagueTeams.TeamData teamData : teams) {
                int teamId = teamData.getTeam().getId();
                String teamName = teamData.getTeam().getName();
                List<APIResponseWrapper.PlayerWrapper> allPlayers = new ArrayList<>();

                int page = 1;
                int totalPages = 1; // will be updated after first response

                do {
                    try {
                        Response<APIResponseWrapper.apiWrapper> response = apiService
                                .getPlayerStats(teamId, season, 39, APIClient.getApiKey(), page)
                                .execute(); // blocking call

                        if (response.isSuccessful() && response.body() != null) {
                            APIResponseWrapper.apiWrapper body = response.body();

                            if (body.getResponse() != null) {
                                allPlayers.addAll(body.getResponse());
                            }

                            // Update total pages on first response
                            if (page == 1 && body.getPaging() != null) {
                                totalPages = body.getPaging().getTotal();
                            }

                            Log.d(TAG, "Fetched page " + page + " for " + teamName);
                        } else {
                            Log.e(TAG, "Failed page " + page + " for " + teamName + ": " + response.message());
                            break;
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Exception fetching page " + page + " for " + teamName, e);
                        break;
                    }

                    page++;

                    if (page <= totalPages) {
                        try {
                            Thread.sleep(6500); // delay between pages
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Sleep interrupted", e);
                        }
                    }

                } while (page <= totalPages);

                teamPlayerMap.put(teamName, allPlayers);
                Log.d(TAG, "Finished " + teamName + " with " + allPlayers.size() + " players");

                completed++;
                if (completed < totalTeams) {
                    try {
                        Thread.sleep(6500); // delay between teams
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Sleep interrupted", e);
                    }
                }
            }

            callback.onSuccess(teamPlayerMap);
        }).start();
    }
    public interface FixtureCallback {
        void onSuccess(List<FixtureResponseWrapper.FixtureData> fixtures);
        void onError(String error);
    }

    public void fetchRecentFixtures(int teamId, int season, FixtureCallback callback) {
        Call<FixtureResponseWrapper.ApiResponse> call = apiService.getRecentFixtures(
                teamId,
                5, // number of recent fixtures
                season,
                39, // leagueId for Premier League
                APIClient.getApiKey()
        );

        call.enqueue(new Callback<FixtureResponseWrapper.ApiResponse>() {
            @Override
            public void onResponse(Call<FixtureResponseWrapper.ApiResponse> call, Response<FixtureResponseWrapper.ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to fetch fixtures: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<FixtureResponseWrapper.ApiResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    public interface HeadToHeadCallback {
        void onSuccess(List<FixtureResponseWrapper.FixtureData> fixtures);
        void onError(String error);
    }
    public void fetchHeadToHeadFixtures(int teamId1, int teamId2, HeadToHeadCallback callback) {
        String h2hParam = teamId1 + "-" + teamId2;

        Call<FixtureResponseWrapper.ApiResponse> call = apiService.getHeadToHeadFixtures(
                h2hParam,
                3, // last 3 fixtures
                APIClient.getApiKey()
        );

        call.enqueue(new Callback<FixtureResponseWrapper.ApiResponse>() {
            @Override
            public void onResponse(Call<FixtureResponseWrapper.ApiResponse> call, Response<FixtureResponseWrapper.ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResponse());
                } else {
                    callback.onError("Failed to fetch head-to-head: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<FixtureResponseWrapper.ApiResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }





}