package com.example.javaonlineproject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Klasa reprezentująca dane związane z historią meczu.
 *
 * <p>Zawiera informacje o dacie meczu, nazwach użytkowników graczy oraz wyniku meczu.
 * Umożliwia serializację i deserializację obiektów za pomocą biblioteki Jackson.</p>
 */
public class MatchHistoryData {

    private String date;
    private String player1username;
    private String player2username;
    private String result;

    /**
     * Konstruktor tworzący obiekt {@code MatchHistoryData}.
     *
     * @param date             data meczu
     * @param player1username  nazwa użytkownika pierwszego gracza
     * @param player2username  nazwa użytkownika drugiego gracza
     * @param result           wynik meczu
     */
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

    /**
     * Pobiera datę meczu.
     *
     * @return data meczu.
     */
    public String getDate() {
        return date;
    }

    /**
     * Ustawia datę meczu.
     *
     * @param date data meczu.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Pobiera nazwę użytkownika pierwszego gracza.
     *
     * @return nazwa użytkownika pierwszego gracza.
     */
    public String getPlayer1username() {
        return player1username;
    }

    /**
     * Ustawia nazwę użytkownika pierwszego gracza.
     *
     * @param player1username nazwa użytkownika pierwszego gracza.
     */
    public void setPlayer1username(String player1username) {
        this.player1username = player1username;
    }

    /**
     * Pobiera nazwę użytkownika drugiego gracza.
     *
     * @return nazwa użytkownika drugiego gracza.
     */
    public String getPlayer2username() {
        return player2username;
    }

    /**
     * Ustawia nazwę użytkownika drugiego gracza.
     *
     * @param player2username nazwa użytkownika drugiego gracza.
     */
    public void setPlayer2username(String player2username) {
        this.player2username = player2username;
    }

    /**
     * Pobiera wynik meczu.
     *
     * @return wynik meczu.
     */
    public String getResult() {
        return result;
    }

    /**
     * Ustawia wynik meczu.
     *
     * @param result wynik meczu.
     */
    public void setResult(String result) {
        this.result = result;
    }
}




