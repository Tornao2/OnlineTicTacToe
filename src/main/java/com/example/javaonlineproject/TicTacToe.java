package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.stage.Stage;

public class TicTacToe extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setOnLoginPlayer(() -> sceneMenu(primaryStage, loginScreen.getUser()));
        loginScreen.setOnLoginServer(() -> serverLogic(primaryStage));
        loginScreen.start(primaryStage);
    }

    private void serverLogic(Stage primaryStage) {
        ServerLogic server = new ServerLogic();
        server.start(primaryStage);
    }

    private void sceneMenu(Stage primaryStage, UserInfo user) {
        Menu menu = new Menu();
        menu.setOnStartSuccess(() -> enemyList(primaryStage, user));
        menu.start(primaryStage, user);
    }

    private void enemyList(Stage primaryStage, UserInfo user) {
        WaitList enemySelection = new WaitList();
        enemySelection.setOnBack(() -> sceneMenu(primaryStage, user));
        enemySelection.start(primaryStage, user);
    }

    /*

    public void sceneGame(Stage primaryStage, Connection connection) {
        Board board = new Board();
        board.start(primaryStage, connection);
    }
    */
}

//Przydałoby się ustandaryzować wygląd