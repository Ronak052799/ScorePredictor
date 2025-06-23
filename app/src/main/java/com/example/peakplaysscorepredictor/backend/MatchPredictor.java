package com.example.peakplaysscorepredictor.backend;
import java.util.List;

public class MatchPredictor {
    public static class PredictionResult {
        public final String winner;
        public final double confidenceScore;

        public PredictionResult(String winner, double confidenceScore) {
            this.winner = winner;
            this.confidenceScore = confidenceScore;
        }
    }

    public PredictionResult predictWinner(
            String teamAName,
            String teamBName,
            double teamARating,
            double teamBRating,
            List<FixtureResponseWrapper.FixtureData> recentFormA,
            List<FixtureResponseWrapper.FixtureData> recentFormB,
            List<FixtureResponseWrapper.FixtureData> headToHead
    ) {
        int scoreA = 0;
        int scoreB = 0;

        // Team Rating Weight (out of 30)
        if (teamARating > teamBRating) scoreA += 30;
        else if (teamARating < teamBRating) scoreB += 30;
        else { scoreA += 15; scoreB += 15; }

        // Recent Form (out of 35)
        scoreA += evaluateRecentForm(recentFormA, teamAName);
        scoreB += evaluateRecentForm(recentFormB, teamBName);

        // Head to Head (out of 35)
        scoreA += evaluateHeadToHead(headToHead, teamAName);
        scoreB += evaluateHeadToHead(headToHead, teamBName);

        // Final Decision
        if (scoreA > scoreB) {
            double confidence = 100.0 * scoreA / (scoreA + scoreB);
            return new PredictionResult(teamAName, confidence);
        } else {
            double confidence = 100.0 * scoreB / (scoreA + scoreB);
            return new PredictionResult(teamBName, confidence);
        }
    }

    private int evaluateRecentForm(List<FixtureResponseWrapper.FixtureData> fixtures, String teamName) {
        int score = 0;
        for (FixtureResponseWrapper.FixtureData fixture : fixtures) {
            boolean isHome = fixture.getTeams().getHome().getName().equalsIgnoreCase(teamName);
            boolean winner = fixture.getTeams().getHome().isWinner() == isHome;

            if (fixture.getTeams().getHome().isWinner() == isHome) score += 7; // Win
            else if (!fixture.getTeams().getHome().isWinner() && !fixture.getTeams().getAway().isWinner()) score += 3; // Draw
            else score += 0; // Loss
        }
        return score; // Max 5 x 7 = 35
    }

    private int evaluateHeadToHead(List<FixtureResponseWrapper.FixtureData> h2hFixtures, String teamName) {
        int score = 0;
        for (FixtureResponseWrapper.FixtureData fixture : h2hFixtures) {
            boolean isHome = fixture.getTeams().getHome().getName().equalsIgnoreCase(teamName);
            boolean winner = fixture.getTeams().getHome().isWinner() == isHome;

            if (fixture.getTeams().getHome().isWinner() == isHome) score += 12; // Win
            else if (!fixture.getTeams().getHome().isWinner() && !fixture.getTeams().getAway().isWinner()) score += 6; // Draw
            else score += 0; // Loss
        }
        return score; // Max 3 x 12 = 36
    }
}
