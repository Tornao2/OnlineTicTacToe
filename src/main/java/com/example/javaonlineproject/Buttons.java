package com.example.javaonlineproject;

import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class Buttons {
    private Button startGameButton;
    private Button exitGameButton;
    private Button restartGameButton;
    private Network network;
    public Buttons() {
        startGameButton = new Button("Start");
        startGameButton.setMinSize(100, 30);
        startGameButton.setTranslateX(450);
        startGameButton.setTranslateY(20);
        startGameButton.setOnAction(event -> startGame());

        restartGameButton = new Button("Restart");
        restartGameButton.setMinSize(100, 30);
        restartGameButton.setTranslateX(450);
        restartGameButton.setTranslateY(20);
        restartGameButton.setOnAction(event -> resetGame());

        exitGameButton = new Button("Exit");
        exitGameButton.setMinSize(100, 30);
        exitGameButton.setTranslateX(450);
        exitGameButton.setTranslateY(20);
        exitGameButton.setOnAction(event -> exitGame());
    }

    private void startGame() {
        TextInputDialog dialog = new TextInputDialog("Server/Client");
        dialog.setTitle("Choose Network mode");
        dialog.setHeaderText("Selcet if you want to start as server or client");
        dialog.setContentText("Enter 'server' or 'client");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(mode -> {
            Boolean isServer = mode.equalsIgnoreCase("server");
            network = new Network(isServer);
            Board gameBoard = new Board(network);
        });
    }

    public Button getStartGameButton() {
        return startGameButton;
    }

    public static void resetGame() {
        System.out.println("JEST");
    }

    public Button getRestartGameButton() {
        return restartGameButton;
    }
    private void exitGame() {
        System.out.println("SUPER");
        System.exit(0);
    }

    public Button getExitGameButton() {
        return exitGameButton;
    }
}
