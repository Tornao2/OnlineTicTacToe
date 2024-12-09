package com.example.javaonlineproject;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.Collections;
import java.util.List;




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


    private VBox createVBoxRight() {
        VBox organizer = new VBox(12);
        organizer.setPrefSize(280, 210);
        organizer.setPadding(new Insets(40, 8, 40, 20));
        organizer.setAlignment(Pos.TOP_RIGHT);
        return organizer;
    }


    private VBox createVBoxLeft() {
        VBox organizer = new VBox(12);
        organizer.setPrefSize(280, 210);
        organizer.setPadding(new Insets(40, 8, 40, 20));
        organizer.setAlignment(Pos.BASELINE_LEFT);
        return organizer;
    }


    private VBox createVBoxBottomCenter() {
        VBox organizer = new VBox(12);
        organizer.setPrefSize(280, 10);
        organizer.setPadding(new Insets(0, 8, 50, 20));
        organizer.setAlignment(Pos.BOTTOM_CENTER);
        return organizer;
    }

    private BorderPane createManager(VBox organizer, VBox organizer2, VBox organizer3) {
        BorderPane root = new BorderPane();
        root.setPrefSize(900, 600);
        root.setCenter(organizer);
        root.setLeft(organizer2);
        root.setBottom(organizer3);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    private void manageScene(BorderPane manager) {
        Scene scene = new Scene(manager);
        primaryStage.setTitle("Stats");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void displayMatchHistory(List<MatchHistoryData> matchHistory, VBox organizer) {
        organizer.getChildren().clear();
        if (matchHistory == null || matchHistory.isEmpty()) {
            organizer.getChildren().add(new Label("No match history found."));
        } else {
            TableView<MatchHistoryData> tableView = new TableView<>();
            TableColumn<MatchHistoryData, String> dateColumn = new TableColumn<>("Date");
            dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
            TableColumn<MatchHistoryData, String> enemyColumn = new TableColumn<>("Enemy");
            enemyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlayer2username()));
            TableColumn<MatchHistoryData, String> resultColumn = new TableColumn<>("Result");
            resultColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getResult()));
            tableView.setStyle("-fx-background-color: #1A1A1A;");
            tableView.getColumns().addAll(dateColumn, enemyColumn, resultColumn);
            tableView.setItems(FXCollections.observableArrayList(matchHistory));
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            ScrollPane scrollPane = new ScrollPane(tableView);
            scrollPane.setPrefSize(100, 500);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            organizer.getChildren().add(scrollPane);
        }
    }


    private void receiveMatchHistoryFromServer(VBox organizer) {
        user.getUserOutput().sendMessage("GETMATCHHISTORY");
        String message = user.getUserInput().receiveMessage();
        if (message.startsWith("MATCHHISTORY: ")) {
            String matchHistoryJson = message.substring("MATCHHISTORY: ".length());
            System.out.println(matchHistoryJson);//DEBUG
            try {
                List<MatchHistoryData> matchHistory = objectMapper.readValue(matchHistoryJson, new TypeReference<>() {
                });
                displayMatchHistory(matchHistory, organizer);
            } catch (IOException e) {
                System.err.println("Error parsing match history: " + e.getMessage());
                organizer.getChildren().add((new Label("Match history parse ERROR")));
            }
        } else {
            System.out.println("Message does not contain match history: " + message);
            organizer.getChildren().add(new Label("No match history found."));
        }
    }
    private void receiveStatsFromServer(VBox organizer) {
        user.getUserOutput().sendMessage("GETSTATS");
        String message = user.getUserInput().receiveMessage();
        System.out.println(message);
        if (message.startsWith("STATS:")) {
            String statsJson = message.substring("STATS:".length());
            System.out.println(statsJson); //debug
            try {
                StatsData statsData = objectMapper.readValue(statsJson, StatsData.class);
                displayStats(Collections.singletonList(statsData), organizer);
            } catch (IOException e) {
                System.err.println("Error Parsing stats: " + e.getMessage());
                organizer.getChildren().add((new Label("Match history parse ERROR")));
            }
        } else {
            System.out.println("Message does not contain statsData " + message);
            organizer.getChildren().add(new Label("No match history found."));
        }
    }
    private void displayStats(List<StatsData> statsData, VBox organizer) {
        if (statsData == null || statsData.isEmpty()) organizer.getChildren().add((new Label("No stats found.")));
        else {
            for (StatsData stats : statsData) {
                String statsDetails = "You: " + "   " + stats.getWins() +
                        "-" + stats.getDraws() +
                        "-" + stats.getLosses();
                Label statsLabel = new Label(statsDetails);
                statsLabel.setFont(new Font(34));
                statsLabel.setTextFill(Color.RED);
                statsLabel.setAlignment(Pos. TOP_LEFT);
                organizer.getChildren().add(statsLabel);
            }
        }
    }
    private void receiveBestPlayersFromServer(VBox organizer) {
        user.getUserOutput().sendMessage("GETBESTPLAYERS");
        String message = user.getUserInput().receiveMessage();
        if (message.startsWith("BESTPLAYERS:")) {
            String statsJson = message.substring("BESTPLAYERS:".length());
            System.out.println(statsJson);
            try {
                if (!statsJson.startsWith("[")) {
                    statsJson = "[" + statsJson + "]";
                }
                List<StatsData> bestPlayer = objectMapper.readValue(statsJson, new TypeReference<List<StatsData>>() {});
                displayBestPlayers(bestPlayer, organizer);
            } catch (IOException e) {
                System.err.println("Error Parsing stats: " + e.getMessage());
            }
        } else {
            System.out.println("Message does not contain statsData " + message);
        }
    }


    private void displayBestPlayers(List<StatsData> bestplayer, VBox organizer) {
        if (bestplayer == null || bestplayer.isEmpty())
            organizer.getChildren().add((new Label("No best player found.")));
        else {
            int i = 1;
            for (StatsData player : bestplayer) {
                    String statsDetails = i + ". Username: " + player.getUsername() +
                            " |Wins: " + player.getWins() +
                            " |Draws" + player.getDraws() +
                            " |Loses" + player.getLosses();
                    Label statsLabel = new Label(statsDetails);
                    statsLabel.setFont(new Font(28));
                    statsLabel.setTextFill(Color.BLUE);
                    organizer.getChildren().add(statsLabel);
                i++;
            }
        }
    }
    public void start(Stage primaryStage, UserInfo user) {
        this.user = user;
        this.primaryStage = primaryStage;
        Button backButton = createBackButton();
        VBox organizerRight = createVBoxRight();
        VBox organizerLeft = createVBoxLeft();
        VBox organizerBottomCenter = createVBoxBottomCenter();
        BorderPane manager = createManager(organizerRight, organizerLeft, organizerBottomCenter);
        organizerBottomCenter.getChildren().add(backButton);
        receiveStatsFromServer(organizerRight);
        receiveMatchHistoryFromServer(organizerLeft);
        receiveBestPlayersFromServer(organizerRight);
        manageScene(manager);
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


