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

    // Map team name -> team id for quick lookup
    private Map<String, Integer> teamNameToId = new HashMap<>();

    // Store player data per team (set on fetchPlayerStatsForAllTeams if used)
    private Map<Integer, List<APIResponseWrapper.PlayerWrapper>> teamPlayerMap = new HashMap<>();

    public FootballRepository() {
        apiService = APIClient.getAPIService();
    }

    // New Team data class
    public static class Team {
        public final int id;
        public final String name;

        public Team(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    // Callback interface for teams
    public interface TeamsCallback {
        void onSuccess(List<Team> teams);
        void onError(String error);
    }

    // Fetch teams only (id + name)
    public void fetchTeams(int season, TeamsCallback callback) {
        Call<APIPremierLeagueTeams> teamsCall = apiService.getTeams(39, season, APIClient.getApiKey());

        teamsCall.enqueue(new Callback<APIPremierLeagueTeams>() {
            @Override
            public void onResponse(Call<APIPremierLeagueTeams> call, Response<APIPremierLeagueTeams> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<APIPremierLeagueTeams.TeamData> teamsData = response.body().getResponse();
                    List<Team> teams = new ArrayList<>();
                    teamNameToId.clear();

                    for (APIPremierLeagueTeams.TeamData teamData : teamsData) {
                        int id = teamData.getTeam().getId();
                        String name = teamData.getTeam().getName();
                        teams.add(new Team(id, name));
                        teamNameToId.put(name, id);
                    }

                    callback.onSuccess(teams);
                } else {
                    callback.onError("Failed to fetch teams: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<APIPremierLeagueTeams> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Keep your existing fetchPlayerStatsForAllTeams if you want, but make sure to use team IDs as keys here
    // Update teamPlayerMap to Map<Integer, List<PlayerWrapper>> for direct ID access

    public interface DataCallback {
        void onSuccess(Map<Integer, List<APIResponseWrapper.PlayerWrapper>> teamPlayerMap);
        void onError(String error);
    }

    public void fetchPlayerStatsForAllTeams(List<Team> teams, int season, DataCallback callback) {
        Map<Integer, List<APIResponseWrapper.PlayerWrapper>> playerMap = new HashMap<>();

        new Thread(() -> {
            int totalTeams = teams.size();
            int completed = 0;

            for (Team team : teams) {
                int teamId = team.id;
                List<APIResponseWrapper.PlayerWrapper> allPlayers = new ArrayList<>();

                int page = 1;
                int totalPages = 1;

                do {
                    try {
                        Response<APIResponseWrapper.apiWrapper> response = apiService
                                .getPlayerStats(teamId, season, 39, APIClient.getApiKey(), page)
                                .execute();

                        if (response.isSuccessful() && response.body() != null) {
                            APIResponseWrapper.apiWrapper body = response.body();

                            if (body.getResponse() != null) {
                                allPlayers.addAll(body.getResponse());
                            }

                            if (page == 1 && body.getPaging() != null) {
                                totalPages = body.getPaging().getTotal();
                            }

                            Log.d(TAG, "Fetched page " + page + " for team id " + teamId);
                        } else {
                            Log.e(TAG, "Failed page " + page + " for team id " + teamId + ": " + response.message());
                            break;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception fetching page " + page + " for team id " + teamId, e);
                        break;
                    }

                    page++;
                    if (page <= totalPages) {
                        try {
                            Thread.sleep(6500);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Sleep interrupted", e);
                        }
                    }
                } while (page <= totalPages);

                playerMap.put(teamId, allPlayers);
                Log.d(TAG, "Finished team id " + teamId + " with " + allPlayers.size() + " players");

                completed++;
                if (completed < totalTeams) {
                    try {
                        Thread.sleep(6500);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Sleep interrupted", e);
                    }
                }
            }

            callback.onSuccess(playerMap);
        }).start();
    }

    // Other existing methods unchanged, but updated to accept IDs where relevant:

    public interface FixtureCallback {
        void onSuccess(List<FixtureResponseWrapper.FixtureData> fixtures);
        void onError(String error);
    }

    public void fetchRecentFixtures(int teamId, int season, FixtureCallback callback) {
        Call<FixtureResponseWrapper.ApiResponse> call = apiService.getRecentFixtures(
                teamId,
                5,
                season,
                39,
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
                3,
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

    // Compute average rating for players of a team by teamId
    public double computeTeamRating(int teamId) {
        List<APIResponseWrapper.PlayerWrapper> players = teamPlayerMap.get(teamId);
        if (players == null || players.isEmpty()) return 0.0;

        double ratingSum = 0.0;
        int count = 0;

        for (APIResponseWrapper.PlayerWrapper player : players) {
            if (player.getStatistics() != null && !player.getStatistics().isEmpty()) {
                String ratingStr = player.getStatistics().get(0).getGames().getRating();
                if (ratingStr != null && !ratingStr.isEmpty()) {
                    try {
                        Double rating = Double.valueOf(ratingStr);
                        if (rating != null) {
                            ratingSum += rating;
                            count++;
                        }
                    } catch (NumberFormatException e) {
                        // ignore unparsable ratings
                    }
                }
            }
        }

        return count == 0 ? 0.0 : ratingSum / count;
    }

    public interface PredictionDataCallback {
        void onSuccess(
                double homeTeamRating,
                double awayTeamRating,
                List<FixtureResponseWrapper.FixtureData> recentHomeFixtures,
                List<FixtureResponseWrapper.FixtureData> recentAwayFixtures,
                List<FixtureResponseWrapper.FixtureData> headToHeadFixtures
        );
        void onError(String error);
    }

    // Updated to accept team IDs instead of names
    public void fetchPredictionData(
            int homeTeamId,
            int awayTeamId,
            int season,
            PredictionDataCallback callback
    ) {
        if (!teamPlayerMap.containsKey(homeTeamId) || !teamPlayerMap.containsKey(awayTeamId)) {
            callback.onError("Player data not loaded for one or both teams. Please fetch player stats first.");
            return;
        }

        double homeRating = computeTeamRating(homeTeamId);
        double awayRating = computeTeamRating(awayTeamId);

        final List<FixtureResponseWrapper.FixtureData>[] recentHomeFixtures = new List[1];
        final List<FixtureResponseWrapper.FixtureData>[] recentAwayFixtures = new List[1];
        final List<FixtureResponseWrapper.FixtureData>[] headToHeadFixtures = new List[1];

        final int[] completedCalls = {0};
        final int totalCalls = 3;

        Runnable tryCallback = () -> {
            completedCalls[0]++;
            if (completedCalls[0] == totalCalls) {
                callback.onSuccess(
                        homeRating,
                        awayRating,
                        recentHomeFixtures[0],
                        recentAwayFixtures[0],
                        headToHeadFixtures[0]
                );
            }
        };

        fetchRecentFixtures(homeTeamId, season, new FixtureCallback() {
            @Override
            public void onSuccess(List<FixtureResponseWrapper.FixtureData> fixtures) {
                recentHomeFixtures[0] = fixtures;
                tryCallback.run();
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });

        fetchRecentFixtures(awayTeamId, season, new FixtureCallback() {
            @Override
            public void onSuccess(List<FixtureResponseWrapper.FixtureData> fixtures) {
                recentAwayFixtures[0] = fixtures;
                tryCallback.run();
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });

        fetchHeadToHeadFixtures(homeTeamId, awayTeamId, new HeadToHeadCallback() {
            @Override
            public void onSuccess(List<FixtureResponseWrapper.FixtureData> fixtures) {
                headToHeadFixtures[0] = fixtures;
                tryCallback.run();
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
}
