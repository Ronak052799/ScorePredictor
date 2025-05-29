package com.example.peakplaysscorepredictor.backend;
import java.util.List;
//api call: https://v3.football.api-sports.io/teams?league=39&season=2023
//This class will take in the team Id from the Team Wrapper class to get The TeamID
//The TeamID will be needed so we can make a api call that gets all of stats from the prem teams that season

//Class: Response wrapper
public class APIResponseWrapper {
    public static class apiWrapper{
        private List<PlayerWrapper> response;

        public List<PlayerWrapper> getResponse() {
            return response;
        }
        public void setResponse(List<PlayerWrapper> response) {
            this.response = response;
        }
    }

    //Class: Player Statistic Wrapper
    public static class PlayerWrapper {
        private Player player;
        private List<Statistics> statistics;

        public Player getPlayer() {
            return player;
        }
        public void setPlayer(Player player) {
            this.player = player;
        }

        public List<Statistics> getStatistics() {
            return statistics;
        }
        public void setStatistics(List<Statistics> statistics) {
            this.statistics = statistics;
        }
    }

    public static class Player {
        private int id;
        private String name;
        private boolean injured;

        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public boolean isInjured() {
            return injured;
        }
        public void setInjured(boolean injured) {
            this.injured = injured;
        }
    }

    public static class Statistics {
        private Games games;
        private Goals goals;
        private Tackles tackles;
        private Duels duels;

        public Games getGames() {
            return games;
        }
        public void setGames(Games games) {
            this.games = games;
        }

        public Goals getGoals() {
            return goals;
        }
        public void setGoals(Goals goals) {
            this.goals = goals;
        }

        public Tackles getTackles() {
            return tackles;
        }
        public void setTackles(Tackles tackles) {
            this.tackles = tackles;
        }

        public Duels getDuels() {
            return duels;
        }
        public void setDuels(Duels duels) {
            this.duels = duels;
        }
    }

    public static class Games {
        private String position;
        private String rating;
        private Integer minutes;

        public String getPosition() {
            return position;
        }
        public void setPosition(String position) {
            this.position = position;
        }

        public String getRating() {
            return rating;
        }
        public void setRating(String rating) {
            this.rating = rating;
        }

        public Integer getMinutes() {
            return minutes;
        }
        public void setMinutes(Integer minutes) {
            this.minutes = minutes;
        }
    }

    public static class Goals {
        private Integer total;
        private Integer assists;
        private Integer saves;
        private Integer conceded;

        public Integer getTotal() { return total; }
        public void setTotal(Integer total) { this.total = total; }

        public Integer getAssists() { return assists; }
        public void setAssists(Integer assists) { this.assists = assists; }

        public Integer getSaves() { return saves; }
        public void setSaves(Integer saves) {
            this.saves = saves; }

        public Integer getConceded() {
            return conceded;
        }
        public void setConceded(Integer conceded) {
            this.conceded = conceded;
        }
    }

    public static class Tackles {
        private Integer total;
        private Integer interceptions;

        public Integer getTotal() {
            return total;
        }
        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getInterceptions() {
            return interceptions;
        }
        public void setInterceptions(Integer interceptions) {
            this.interceptions = interceptions;
        }
    }

    public static class Duels {
        private Integer total;
        private Integer won;

        public Integer getTotal() {
            return total;
        }
        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getWon() {
            return won;
        }
        public void setWon(Integer won) {
            this.won = won;
        }
    }
}

