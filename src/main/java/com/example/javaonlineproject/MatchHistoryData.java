package com.example.javaonlineproject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MatchHistoryData {
    private String date;
    private String player1username;
    private String player2username;
    private String result;

    @JsonCreator
    public MatchHistoryData(
            @JsonProperty("date") String date,
            @JsonProperty("player1username") String player1username,
            @JsonProperty("player2username") String player2username,
            @JsonProperty("result") String result) {
        this.date = date;
        this.player1username = player1username;
        this.player2username = player2username;
        this.result = result;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getPlayer1username() {
        return player1username;
    }
    public void setPlayer1username(String player1username) {
        this.player1username = player1username;
    }
    public String getPlayer2username() {
        return player2username;
    }
    public void setPlayer2username(String player2username) {
        this.player2username = player2username;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
}
