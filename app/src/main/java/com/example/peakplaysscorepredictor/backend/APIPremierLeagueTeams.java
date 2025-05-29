package com.example.peakplaysscorepredictor.backend;

import java.util.List;

public class APIPremierLeagueTeams {
    private List<TeamData> response;

    public List<TeamData> getResponse() {
        return response;
    }

    public void setResponse(List<TeamData> response) {
        this.response = response;
    }

    public static class TeamData {
        private Team team;

        public Team getTeam() {
            return team;
        }

        public void setTeam(Team team) {
            this.team = team;
        }
    }

    public static class Team {
        private int id;
        private String name;
        private String logo;

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

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }
}