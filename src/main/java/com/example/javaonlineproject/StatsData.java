package com.example.javaonlineproject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Klasa reprezentuje dane statystyczne użytkownika, takie jak liczba zwycięstw, remisów i porażek.
 */
public class StatsData {

    /**
     * Nazwa użytkownika.
     */
    private String username;

    /**
     * Liczba zwycięstw użytkownika.
     */
    private int wins;

    /**
     * Liczba remisów użytkownika.
     */
    private int draws;

    /**
     * Liczba porażek użytkownika.
     */
    private int losses;

    /**
     * Konstruktor klasy StatsData inicjalizujący wszystkie pola na podstawie podanych parametrów.
     *
     * @param username nazwa użytkownika
     * @param wins liczba zwycięstw
     * @param draws liczba remisów
     * @param losses liczba porażek
     */
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

    /**
     * Pobiera nazwę użytkownika.
     *
     * @return nazwa użytkownika
     */
    public String getUsername() {
        return username;
    }

    /**
     * Pobiera liczbę zwycięstw.
     *
     * @return liczba zwycięstw
     */
    public int getWins() {
        return wins;
    }

    /**
     * Pobiera liczbę remisów.
     *
     * @return liczba remisów
     */
    public int getDraws() {
        return draws;
    }

    /**
     * Pobiera liczbę porażek.
     *
     * @return liczba porażek
     */
    public int getLosses() {
        return losses;
    }

    /**
     * Ustawia nazwę użytkownika.
     *
     * @param username nowa nazwa użytkownika
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Ustawia liczbę zwycięstw.
     *
     * @param wins nowa liczba zwycięstw
     */
    public void setWins(int wins) {
        this.wins = wins;
    }

    /**
     * Ustawia liczbę remisów.
     *
     * @param draws nowa liczba remisów
     */
    public void setDraws(int draws) {
        this.draws = draws;
    }

    /**
     * Ustawia liczbę porażek.
     *
     * @param losses nowa liczba porażek
     */
    public void setLosses(int losses) {
        this.losses = losses;
    }

    /**
     * Zwiększa liczbę zwycięstw o 1.
     */
    public void incrementWins() {
        this.wins++;
    }

    /**
     * Zwiększa liczbę remisów o 1.
     */
    public void incrementDraws() {
        this.draws++;
    }

    /**
     * Zwiększa liczbę porażek o 1.
     */
    public void incrementLosses() {
        this.losses++;
    }
}



