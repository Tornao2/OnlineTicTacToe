package com.example.javaonlineproject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StatsData {
    private String username;
    private int wins;
    private int draws;
    private int losses;

    @JsonCreator
    public StatsData(@JsonProperty("username") String username,
                     @JsonProperty("wins") int wins,
                     @JsonProperty("draws") int draws,
                     @JsonProperty("losses") int losses) {
        this.username = username;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
    }

    public String getUsername() {
        return username;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void incrementWins() {
        this.wins++;
    }

    public void incrementDraws() {
        this.draws++;
    }

    public void incrementLosses() {
        this.losses++;
    }
}
