package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private Network network;
    public static final int screenW = 800;
    public static final int screenH = 600;
    MouseHandler msHandler;

    @Override
    public void start(Stage primaryStage) {
        showLoginScreen(primaryStage);
    }

    private void showLoginScreen(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setOnLoginSuccess(() -> showMenu(primaryStage)); // Set the callback for successful login
        loginScreen.start(primaryStage); // Start the login screen
    }

    private void showMenu(Stage primaryStage) {
        System.out.println("Loading Menu..."); // Debug line
        Menu menu = new Menu();
        menu.start(primaryStage);
        menu.getStartButton().setOnAction(event -> showNetworkSelection(primaryStage));
    }


    private void showNetworkSelection(Stage primaryStage) {
        NetworkModeSelection modeSelection = new NetworkModeSelection();
        modeSelection.show(primaryStage, isServer -> initializeGame(primaryStage, isServer));
    }

    public void initializeGame(Stage primaryStage, boolean isServer) {
        StackPane root = new StackPane();
        msHandler = new MouseHandler();
        msHandler.setEvents(root);

        Buttons buttons =   new Buttons(new Menu());
        network = new Network(isServer);
        Board gameBoard = new Board(network);

        root.getChildren().add(gameBoard.getRootPane());

        Scene scene = new Scene(root, screenW, screenH);
        primaryStage.setTitle("Tic-Tac-Toe Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}