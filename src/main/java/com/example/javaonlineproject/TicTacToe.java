package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.stage.Stage;

public class TicTacToe extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setOnLoginPlayer(() -> sceneMenu(primaryStage, loginScreen.getUser()));
        loginScreen.start(primaryStage);
    }

    private void sceneMenu(Stage primaryStage, UserInfo user) {
        Menu menu = new Menu();
        menu.setOnStartSuccess(() -> enemyList(primaryStage, user));
        menu.setOnDisconnect(() ->start(primaryStage));
        menu.setOnStats(()->sceneStats(primaryStage, user));
        menu.start(primaryStage, user);
    }

    private void sceneStats(Stage primaryStage, UserInfo user) {
        Stats stats = new Stats();
        stats.setOnBack(() -> sceneMenu(primaryStage, user));
        stats.setOnDisconnect(() ->start(primaryStage));
        stats.start(primaryStage, user);
    }

    private void enemyList(Stage primaryStage, UserInfo user) {
        WaitList enemySelection = new WaitList();
        enemySelection.setOnBack(() -> sceneMenu(primaryStage, user));
        enemySelection.setOnPlay(() -> playMatch(primaryStage, user, enemySelection.getSymbols()));
        enemySelection.setOnDisconnect(() -> start(primaryStage));
        enemySelection.start(primaryStage, user);
    }

    private void playMatch(Stage primaryStage, UserInfo user, String[] usedSymbols) {
        Board board = new Board();
        board.setOnResign(() -> sceneMenu(primaryStage, user));
        board.setOnDisconnect(() -> start(primaryStage));
        board.start(primaryStage, user, usedSymbols);
    }
}
