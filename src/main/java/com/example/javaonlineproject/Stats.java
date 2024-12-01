package com.example.javaonlineproject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.List;
/*
(nie wyswietla sie back button) Historia sie wyswietla nie byla testowana, kod do wyczyszczenia
 */
public class Stats {
    private Runnable onBack;
    private Runnable onDisconnect;
    private UserInfo user;
    private Thread disconnectThread;
    private ObjectMapper objectMapper = new ObjectMapper();
    private Stage primaryStage;

    private Button createBackButton() {
        Button backButton = new Button("Back");
        backButton.setFont(new Font(16.0));
        backButton.setOnAction(_ -> backButton());
        return backButton;
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
        Scene scene = new Scene(manager);
        primaryStage.setTitle("Stats");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayMatchHistory(List<MatchHistoryData> matchHistory) {
        VBox organizer = createVBox();

        if (matchHistory == null || matchHistory.isEmpty()) {
            organizer.getChildren().add(new Label("No match history found."));
        } else {
            for (MatchHistoryData match : matchHistory) {
                String matchDetails = "Date: " + match.getDate() +
                        " | Player: " + match.getPlayer1username() +
                        " | Enemy: " + match.getPlayer2username() +
                        " | Result: " + match.getResult();
                Label matchLabel = new Label(matchDetails);
                matchLabel.setFont(new Font(14.0));
                matchLabel.setTextFill(Color.WHITE);
                organizer.getChildren().add(matchLabel);
            }
        }

        BorderPane root = createManager(organizer);
        manageScene(primaryStage, root);
    }

    private void reciveMatchHistoryFromServer() {
        Runnable matchHistoryReceiver = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                user.getUserOutput().sendMessage("GETMATCHHISTORY");
                String message = user.getUserInput().receiveMessage();

                if (message == null) continue;

                if (message.startsWith("MATCHHISTORY: ")) {
                    String matchHistoryJson = message.substring("MATCHHISTORY: ".length());

                    try {
                        List<MatchHistoryData> matchHistory = objectMapper.readValue(matchHistoryJson, new TypeReference<List<MatchHistoryData>>() {});
                        Platform.runLater(() -> displayMatchHistory(matchHistory));
                    } catch (IOException e) {
                        System.err.println("Error parsing match history: " + e.getMessage());
                    }
                } else {
                    System.out.println("Message does not contain match history: " + message);
                }
            }
        };

        Thread historyThread = new Thread(matchHistoryReceiver);
        historyThread.setDaemon(true);
        historyThread.start();
    }

    public void start(Stage primaryStage, UserInfo user) {
        this.user = user;
        this.primaryStage = primaryStage;

        Button backButton = createBackButton();
        VBox organizer = createVBox();
        organizer.getChildren().add(backButton);
        BorderPane manager = createManager(organizer);

        user.getUserOutput().sendMessage("GETMATCHHISTORY");
        reciveMatchHistoryFromServer();

        manageScene(primaryStage, manager);
        checkForDisconnect();
    }

    private void checkForDisconnect() {
        Runnable disconnectChecker = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                String move = user.getUserInput().receiveMessage();
                if (move == null) continue;
                else if (move.equals("SOCKETERROR")) {
                    Platform.runLater(Stats.this::disconnect);
                    return;
                }
            }
        };
        disconnectThread = new Thread(disconnectChecker);
        disconnectThread.setDaemon(true);
        disconnectThread.start();
    }

    private void backButton() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException _) {}
        onBack.run();
    }

    private void disconnect() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException _) {}
        user.closeConnection();
        onDisconnect.run();
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    public void setOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect = onDisconnect;
    }
}
