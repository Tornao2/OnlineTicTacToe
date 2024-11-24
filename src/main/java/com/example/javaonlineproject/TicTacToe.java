package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.stage.Stage;

public class TicTacToe extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setOnLoginPlayer(() -> sceneMenu(primaryStage));
        loginScreen.setOnLoginServer(() -> serverLogic(primaryStage));
        loginScreen.start(primaryStage);
    }

    private void serverLogic(Stage primaryStage) {
        ServerLogic server = new ServerLogic();
        server.start(primaryStage);
    }

    private void sceneMenu(Stage primaryStage) {
        Menu menu = new Menu();
        menu.setOnStartSuccess(null);
        menu.start(primaryStage);
    }

    /*
    private void sceneNetworkSelect(Stage primaryStage) {
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
    */
}
