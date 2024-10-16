package com.example.javaonlineproject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HelloApplication extends Application {
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

        Board gameBoard = new Board();

        VBox layout = new VBox(10);
        layout.getChildren().addAll(startButton, resetButton, exitButton, gameBoard.getGridPane());

        root.getChildren().add(layout);

        Scene scene = new Scene(root, screenW, screenH);
        primaryStage.setTitle("Project");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
