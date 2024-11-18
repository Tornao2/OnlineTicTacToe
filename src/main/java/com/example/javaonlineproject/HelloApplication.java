package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    public static final int screenW = 800;
    public static final int screenH = 600;
    @Override
    public void start(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setOnLoginSuccess(() -> showMenu(primaryStage)); // Set the callback for successful login
        loginScreen.start(primaryStage); // Start the login screen
    }
    private void showMenu(Stage primaryStage) {
        System.out.println("Loading Menu..."); // Debugging
        Menu menu = new Menu();
        menu.setOnStartSuccess(() -> showNetworkSelection(primaryStage));
        menu.start(primaryStage);
    }
    private void showNetworkSelection(Stage primaryStage) {
        System.out.println("Loading Network Selection..."); // Debugging
        NetworkModeSelection modeSelection = new NetworkModeSelection();
        modeSelection.setOnStartSuccess(() -> initializeGame(primaryStage, modeSelection.getIsServer()));
        modeSelection.start(primaryStage);
    }

    public void initializeGame(Stage primaryStage, boolean isServer) {
        System.out.println(isServer);
        Network network;
        StackPane root = new StackPane();
        network = new Network(isServer);
        Board gameBoard = new Board(network);
        root.getChildren().add(gameBoard.getRootPane());
        Scene scene = new Scene(root, screenW, screenH);
        primaryStage.setTitle("Tic-Tac-Toe Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}