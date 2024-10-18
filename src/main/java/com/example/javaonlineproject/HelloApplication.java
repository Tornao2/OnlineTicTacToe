package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class HelloApplication extends Application {
    private Network network;
    public static final int screenW = 900;
    public static final int screenH = 700;
    MouseHandler msHandler;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        msHandler = new MouseHandler();
        msHandler.setEvents(root);

        Buttons buttons = new Buttons();
        Button startButton = buttons.getStartGameButton();
        Button resetButton = buttons.getRestartGameButton();
        Button exitButton = buttons.getExitGameButton();

        // Ask the user if they want to start as a server or client
        TextInputDialog dialog = new TextInputDialog("Server/Client");
        dialog.setTitle("Choose Network Mode");
        dialog.setHeaderText("Select if you want to run as a server or client");
        dialog.setContentText("Enter 'server' or 'client':");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(mode -> {
            boolean isServer = mode.equalsIgnoreCase("server");
            network = new Network(isServer); // Initialize the network with user input
            Board gameBoard = new Board(network); // Pass the network to the board

            VBox layout = new VBox(10);
            layout.getChildren().addAll(startButton, resetButton, exitButton, gameBoard.getGridPane());

            root.getChildren().add(layout);

            Scene scene = new Scene(root, screenW, screenH);
            primaryStage.setTitle("Project");
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
