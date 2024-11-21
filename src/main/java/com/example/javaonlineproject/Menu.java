package com.example.javaonlineproject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Menu  {
    private Runnable onStartSuccess;

    private Button createStartButton() {
        Button startButton = new Button("Start");
        startButton.setFont(new Font(16.0));
        startButton.setOnAction(_ -> ChangeScene());
        return startButton;
    }
    private Button createStatsButton() {
        Button statsButton = new Button("Stats");
        statsButton.setFont(new Font(16.0));
        statsButton.setOnAction(_ -> statsButton());
        return statsButton;
    }
    private Button createQuitButton() {
        Button quitButton = new Button("Quit");
        quitButton.setFont(new Font(16.0));
        quitButton.setOnAction(_ -> quitButton());
        return quitButton;
    }
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setPrefSize(300, 180);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        organizer.setAlignment(Pos.BASELINE_CENTER);
        return organizer;
    }
    private BorderPane createManager(VBox organizer) {
        BorderPane root = new BorderPane(organizer);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    private void manageScene(Stage primaryStage, BorderPane manager) {
        Scene scene = new Scene(manager);
        primaryStage.setTitle("Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void start(Stage primaryStage) {
        Button startButton = createStartButton();
        Button statsButton = createStatsButton();
        Button quitButton = createQuitButton();
        VBox organizer = createVBox();
        organizer.getChildren().addAll(startButton, statsButton, quitButton);
        BorderPane manager = createManager(organizer);
        manageScene(primaryStage, manager);
    }

    private void ChangeScene() {
        onStartSuccess.run();
    }
    public static void statsButton() {

    }
    private void quitButton() {
        System.exit(0);
    }

    public void setOnStartSuccess(Runnable onLoginSuccess) {
        this.onStartSuccess = onLoginSuccess;
    }
}