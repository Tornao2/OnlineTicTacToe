package com.example.javaonlineproject;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Menu  {
    private Thread disconnectThread;
    private Runnable onStartSuccess;
    private Runnable onStats;
    private UserInfo user;

    private Text createWelcomeText() {
        Text text = new Text("Welcome " + user.getUsername() + "!");
        text.getStyleClass().add("welcome-text");
        text.setFill(javafx.scene.paint.Color.WHITE);
        return text;
    }
    private Button createStartButton() {
        Button startButton = new Button("Start");
        startButton.setFont(new Font(32.0));
        startButton.getStyleClass().add("button");
        startButton.setPrefSize(250,80);
        startButton.setOnAction(_ -> ChangeScene());
        return startButton;
    }
    private Button createStatsButton() {
        Button statsButton = new Button("Stats");
        statsButton.setFont(new Font(32.0));
        statsButton.getStyleClass().add("button");
        statsButton.setPrefSize(250,80);
        statsButton.setOnAction(_ -> statsButton());
        return statsButton;
    }
    private Button createQuitButton() {
        Button quitButton = new Button("Quit");
        quitButton.setFont(new Font(32.0));
        quitButton.getStyleClass().add("button");
        quitButton.setPrefSize(250,80);
        quitButton.setOnAction(_ -> quitButton());
        return quitButton;
    }
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setPrefSize(280, 210);
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
        Scene scene = new Scene(manager, 400, 400);
        primaryStage.setTitle("Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    public void start(Stage primaryStage, UserInfo user) {
        this.user = user;
        Text welcomeText = createWelcomeText();
        Button startButton = createStartButton();
        Button statsButton = createStatsButton();
        Button quitButton = createQuitButton();
        VBox organizer = createVBox();
        organizer.getChildren().addAll(welcomeText, startButton, statsButton, quitButton);
        BorderPane manager = createManager(organizer);
        manageScene(primaryStage, manager);
        checkForDisconnect();
    }
    private void checkForDisconnect() {
        Runnable disconnectChecker = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                String move = user.getUserInput().receiveMessage();
                if (move == null) continue;
                if (move.equals("SOCKETERROR") || move.equals("CLOSING")) {
                    Platform.runLater(Menu.this::disconnect);
                    return;
                }
            }
        };
        disconnectThread = new Thread(disconnectChecker);
        disconnectThread.setDaemon(true);
        disconnectThread.start();
    }
    private void ChangeScene() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException _) {}
        onStartSuccess.run();
    }
    private void statsButton() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException _) {}
        onStats.run();
    }
    private void quitButton() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException _) {}
        user.getUserOutput().sendMessage("SOCKETERROR");
        user.closeConnection();
        System.exit(0);
    }
    private void disconnect() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException _) {}
        user.closeConnection();
        System.exit(-2);
    }
    public void setOnStartSuccess(Runnable onLoginSuccess) {
        this.onStartSuccess = onLoginSuccess;
    }
    public void setOnStats(Runnable onStats) {
        this.onStats = onStats;
    }
}