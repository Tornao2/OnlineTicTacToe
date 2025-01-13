package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Główna klasa aplikacji Tic Tac Toe, rozszerzająca klasę {@link Application} JavaFX.
 * Odpowiada za zarządzanie przepływem między ekranami aplikacji.
 */
public class TicTacToe extends Application {
    /**
     * Metoda startowa aplikacji JavaFX, inicjalizuje pierwszy ekran - ekran logowania.
     *
     * @param primaryStage główna scena aplikacji JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setOnLoginPlayer(() -> sceneMenu(primaryStage, loginScreen.getUser()));
        loginScreen.start(primaryStage);
    }
    /**
     * Przełącza scenę na menu główne użytkownika.
     *
     * @param primaryStage główna scena aplikacji JavaFX
     * @param user obiekt {@link UserInfo} reprezentujący zalogowanego użytkownika
     */
    private void sceneMenu(Stage primaryStage, UserInfo user) {
        Menu menu = new Menu();
        menu.setOnStartSuccess(() -> enemyList(primaryStage, user));
        menu.setOnStats(() -> sceneStats(primaryStage, user));
        menu.start(primaryStage, user);
    }
    /**
     * Przełącza scenę na ekran statystyk użytkownika.
     *
     * @param primaryStage główna scena aplikacji JavaFX
     * @param user obiekt {@link UserInfo} reprezentujący zalogowanego użytkownika
     */
    private void sceneStats(Stage primaryStage, UserInfo user) {
        Stats stats = new Stats();
        stats.setOnBack(() -> sceneMenu(primaryStage, user));
        stats.setOnDisconnect(() -> sceneMenu(primaryStage, user));
        stats.start(primaryStage, user);
    }
    /**
     * Przełącza scenę na ekran listy przeciwników.
     *
     * @param primaryStage główna scena aplikacji JavaFX
     * @param user obiekt {@link UserInfo} reprezentujący zalogowanego użytkownika
     */
    private void enemyList(Stage primaryStage, UserInfo user) {
        WaitList enemySelection = new WaitList();
        enemySelection.setOnBack(() -> sceneMenu(primaryStage, user));
        enemySelection.setOnPlay(() -> playMatch(primaryStage, user, enemySelection.getSymbols()));
        enemySelection.start(primaryStage, user);
    }
    /**
     * Przełącza scenę na ekran rozgrywki.
     *
     * @param primaryStage główna scena aplikacji JavaFX
     * @param user obiekt {@link UserInfo} reprezentujący zalogowanego użytkownika
     * @param usedSymbols symbole wybrane do rozgrywki
     */
    private void playMatch(Stage primaryStage, UserInfo user, String[] usedSymbols) {
        Board board = new Board();
        board.setOnResign(() -> sceneMenu(primaryStage, user));
        board.start(primaryStage, user, usedSymbols);
    }
}







