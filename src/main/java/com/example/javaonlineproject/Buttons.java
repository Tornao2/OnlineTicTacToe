package com.example.javaonlineproject;

import javafx.scene.control.Button;

public class Buttons {
    private Button startGameButton;
    private Button exitGameButton;
    private Button restartGameButton;
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
        System.out.println("ALLEGRO");
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
