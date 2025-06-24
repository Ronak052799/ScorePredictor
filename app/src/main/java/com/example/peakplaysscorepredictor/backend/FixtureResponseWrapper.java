package com.example.peakplaysscorepredictor.backend;

import java.util.List;

public class FixtureResponseWrapper {

    public static class ApiResponse {
        private List<FixtureData> response;

        public List<FixtureData> getResponse() {
            return response;
        }

        public void setResponse(List<FixtureData> response) {
            this.response = response;
        }
    }

    public static class FixtureData {
        private Fixture fixture;
        private Teams teams;
        private Goals goals;

        public Fixture getFixture() {
            return fixture;
        }

        public void setFixture(Fixture fixture) {
            this.fixture = fixture;
        }

        public Teams getTeams() {
            return teams;
        }

        public void setTeams(Teams teams) {
            this.teams = teams;
        }

        public Goals getGoals() {
            return goals;
        }

        public void setGoals(Goals goals) {
            this.goals = goals;
        }
    }

    public static class Fixture {
        private int id;
        private String date;
        private String status;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class Teams {
        private Team home;
        private Team away;

        public Team getHome() {
            return home;
        }

        public void setHome(Team home) {
            this.home = home;
        }

        public Team getAway() {
            return away;
        }

        public void setAway(Team away) {
            this.away = away;
        }
    }

    public static class Team {
        private int id;
        private String name;
        private boolean winner;

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

        public boolean isWinner() {
            return winner;
        }

        public void setWinner(boolean winner) {
            this.winner = winner;
        }
    }

    public static class Goals {
        private Integer home;
        private Integer away;

        public Integer getHome() {
            return home;
        }

        public void setHome(Integer home) {
            this.home = home;
        }

        public Integer getAway() {
            return away;
        }

        public void setAway(Integer away) {
            this.away = away;
        }
    }
}

