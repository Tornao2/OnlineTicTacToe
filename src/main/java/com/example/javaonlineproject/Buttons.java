package com.example.javaonlineproject;

import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;

public class Buttons {
    private Button startGameButton;
    private Button exitGameButton;
    private Button restartGameButton;

    public Buttons() {
        startGameButton = new Button("Start");
        startGameButton.setMinSize(100, 30);
        startGameButton.setOnAction(event -> startGame());

        restartGameButton = new Button("Stats");
        restartGameButton.setMinSize(100, 30);
        restartGameButton.setOnAction(event -> resetGame());

        exitGameButton = new Button("Exit");
        exitGameButton.setMinSize(100, 30);
        exitGameButton.setOnAction(event -> exitGame());
    }

    private void startGame() {
        System.out.println("Game started!");
    }

    public Button getStartGameButton() {
        return startGameButton;
    }

    public static void resetGame() {
        System.out.println("...");
    }

    public Button getRestartGameButton() {
        return restartGameButton;
    }

    private void exitGame() {
        System.out.println("Exiting...");
        System.exit(0);
    }

    public Button getExitGameButton() {
        return exitGameButton;
    }
}
