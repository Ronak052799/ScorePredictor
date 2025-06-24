package com.example.peakplaysscorepredictor.backend;

import java.util.List;

public class TeamRatingCalculator {
    public static double calculateWeightedAverage(List<APIResponseWrapper.PlayerWrapper> players){
        double totalRatingMinutes = 0.0;
        double totalMinutes = 0.0;

        for(APIResponseWrapper.PlayerWrapper player : players){
            APIResponseWrapper.Statistics stats = player.getStatistics().get(0);
            if(stats != null && stats.getGames() != null && stats.getGames().getRating() != null && stats.getGames().getMinutes() != null){
                try{
                    double rating = Double.parseDouble(stats.getGames().getRating());
                    int minutes = stats.getGames().getMinutes();

                    totalRatingMinutes += rating * minutes;
                    totalMinutes += minutes;
                }
                catch( NumberFormatException e){
                    System.err.println("Invalid rating for player: " + player.getPlayer().getName());
                    // optionally: continue; or just let this player be skipped
                }
            }
        }
        return totalMinutes > 0 ? totalRatingMinutes / totalMinutes : 0.0;
    }
}
