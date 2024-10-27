package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Menu extends Application  {
    private Button startButton;
    private Button statsButton;
    private Button quitButton;
    private AnchorPane root;

    @Override
    public void start(Stage primaryStage) {
        initializeMenu();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void initializeMenu() {
        root = new AnchorPane();
        root.setPrefSize(800, 600);
        root.setStyle("-fx-background-color: #1A1A1A;");

        // Initialize buttons
        startButton = new Button("Start");
        startButton.setLayoutX(342.0);
        startButton.setLayoutY(204.0);
        startButton.setMinSize(115.0, 30.0);
        startButton.setFont(new Font(16.0));
        startButton.setOnAction(event -> startButton());

        statsButton = new Button("Stats");
        statsButton.setLayoutX(342.0);
        statsButton.setLayoutY(263.0);
        statsButton.setMinSize(115.0, 30.0);
        statsButton.setFont(new Font(16.0));
        statsButton.setOnAction(event -> statsButton());

        quitButton = new Button("Quit");
        quitButton.setLayoutX(342.0);
        quitButton.setLayoutY(331.0);
        quitButton.setMinSize(115.0, 30.0);
        quitButton.setFont(new Font(16.0));
        quitButton.setOnAction(event -> quitButton());

        // Add buttons to the root pane
        root.getChildren().addAll(startButton, statsButton, quitButton);
    }

    private void startButton() {
        System.out.println("Game started!");

        Network network = new Network(true);
        Board board = new Board(network);

        AnchorPane boardRoot = new AnchorPane();
        boardRoot.getChildren().add(board.getRootPane());

        Scene boardScene = new Scene(boardRoot, 800, 600);

        // Switch to the new scene
        Stage primaryStage = (Stage) startButton.getScene().getWindow();
        primaryStage.setScene(boardScene);
    }

    public static void statsButton() {
        System.out.println("Resetting game...");
    }

    private void quitButton() {
        System.out.println("Exiting...");
        System.exit(0);
    }

        public Button getStartButton() {
        return startButton;
    }

    public Button getStatsButton() {
        return statsButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }

    public static void main(String[] args) {
        launch(args);
    }
}