package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class TicTacToe extends Application {
    public static final int screenW = 800;
    public static final int screenH = 600;
    @Override
    public void start(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setOnLoginSuccess(() -> sceneMenu(primaryStage)); // Set the callback for successful login
        loginScreen.start(primaryStage); // Start the login screen
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
        System.out.println("Connecting..."); // Debugging
        Connection connection = new Connection(isServer);

    }

    public void sceneGame(Stage primaryStage, boolean isServer) {
        StackPane root = new StackPane();
        Connection network = new Connection(isServer);
        Board gameBoard = new Board(network);
        root.getChildren().add(gameBoard.getRootPane());
        Scene scene = new Scene(root, screenW, screenH);
        primaryStage.setTitle("Tic-Tac-Toe Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
