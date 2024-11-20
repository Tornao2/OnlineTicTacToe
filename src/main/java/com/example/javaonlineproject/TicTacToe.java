package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.stage.Stage;

public class TicTacToe extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setOnLoginSuccess(() -> sceneMenu(primaryStage));
        loginScreen.start(primaryStage);
    }
    private void sceneMenu(Stage primaryStage) {
        System.out.println("Loading Menu..."); // Debugging
        Menu menu = new Menu();
        menu.setOnStartSuccess(() -> sceneNetworkSelect(primaryStage));
        menu.start(primaryStage);
    }
    private void sceneNetworkSelect(Stage primaryStage) {
        System.out.println("Loading Network Selection..."); // Debugging
        NetworkModeSelection modeSelection = new NetworkModeSelection();
        modeSelection.setOnStartSuccess(() -> sceneNetworkConnecting(primaryStage, modeSelection.getIsServer()));
        modeSelection.start(primaryStage);
    }

    private void sceneNetworkConnecting(Stage primaryStage, boolean isServer) {
        Connection connection = new Connection(isServer);
        connection.setOnConnectionSuccess(() -> sceneGame(primaryStage, connection));
        connection.start();
    }

    public void sceneGame(Stage primaryStage, Connection connection) {
        Board board = new Board();
        board.start(primaryStage, connection);
    }
}
