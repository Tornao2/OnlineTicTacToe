package com.example.javaonlineproject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Stage primaryStage;

    private Button createBackButton() {
        Button backButton = new Button("Back");
        backButton.setFont(new Font(32.0));
        backButton.getStyleClass().add("button");
        backButton.setOnAction(_ -> backButton());
        return backButton;
    }

    private HBox createHBox() {
        HBox organizer = new HBox(12);
        organizer.setAlignment(Pos.CENTER);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        return organizer;
    }

    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setAlignment(Pos.CENTER);
        organizer.setPadding(new Insets(4, 8, 40, 2));
        return organizer;
    }

    private BorderPane createManager(VBox overallOrganizer) {
        BorderPane root = new BorderPane(overallOrganizer);
        root.setPrefSize(700, 500);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }

    private void manageScene(BorderPane manager) {
        Scene scene = new Scene(manager);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Stats");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void receiveMatchHistoryFromServer(VBox organizer) {
        user.getUserOutput().sendMessage("GETMATCHHISTORY");
        String message = user.getUserInput().receiveMessage();
        if (message.startsWith("MATCHHISTORY: ")) {
            String matchHistoryJson = message.substring("MATCHHISTORY: ".length());
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
            dateColumn.setSortType(TableColumn.SortType.ASCENDING);
            tableView.getSortOrder().add(dateColumn);
            ScrollPane scrollPane = new ScrollPane(tableView);
            scrollPane.setPrefSize(400, 1050);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            organizer.getChildren().add(scrollPane);
        }
    }

    private void receiveStatsFromServer(VBox organizer) {
        user.getUserOutput().sendMessage("GETSTATS");
        String message = user.getUserInput().receiveMessage();
        if (message != null && message.startsWith("STATS:")) {
            String statsJson = message.substring("STATS:".length());
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
            try {
                if (!statsJson.startsWith("[")) {
                    statsJson = "[" + statsJson + "]";
                }
                List<StatsData> bestPlayer = objectMapper.readValue(statsJson, new TypeReference<>() {});
                displayBestPlayers(bestPlayer, organizer);
            } catch (IOException e) {
                System.err.println("Error Parsing stats: " + e.getMessage());
            }
        } else {
            System.out.println("Message does not contain statsData " + message);
        }
    }

    private void displayBestPlayers(List<StatsData> bestPlayer, VBox organizer) {
        organizer.getChildren().clear();
        if (bestPlayer == null || bestPlayer.isEmpty()) {
            organizer.getChildren().add(new Label("No best player found."));
        } else {
            TableView<StatsData> tableView = new TableView<>();
            TableColumn<StatsData, String> usernameColumn = new TableColumn<>("Username");
            usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));

            TableColumn<StatsData, Integer> winsColumn = new TableColumn<>("Wins");
            winsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getWins()).asObject());
            winsColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setText(item.toString());
                        setStyle("-fx-background-color: #28a745;"); // Zielony kolor
                    } else {
                        setText(null);
                        setStyle("");
                    }
                }
            });

            TableColumn<StatsData, Integer> drawsColumn = new TableColumn<>("Draws");
            drawsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDraws()).asObject());
            drawsColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setText(item.toString());
                        setStyle("-fx-background-color: #6c757d;"); // Szary kolor
                    } else {
                        setText(null);
                        setStyle("");
                    }
                }
            });

            TableColumn<StatsData, Integer> lossesColumn = new TableColumn<>("Losses");
            lossesColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLosses()).asObject());
            lossesColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty) {
                        setText(item.toString());
                        setStyle("-fx-background-color: #dc3545;"); // Czerwony kolor
                    } else {
                        setText(null);
                        setStyle("");
                    }
                }
            });

            tableView.getColumns().addAll(usernameColumn, winsColumn, drawsColumn, lossesColumn);
            tableView.setItems(FXCollections.observableArrayList(bestPlayer));
            tableView.setStyle("-fx-background-color: #1A1A1A;");
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            ScrollPane scrollPane = new ScrollPane(tableView);
            scrollPane.setPrefSize(200, 250);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            organizer.getChildren().add(scrollPane);
        }
    }


    public void start(Stage primaryStage, UserInfo user) {
        this.user = user;
        this.primaryStage = primaryStage;
        VBox overallOrganizer = createVBox();
        HBox backButtonBox = createHBox();
        Button backButton = createBackButton();
        backButtonBox.getChildren().add(backButton);
        HBox topSection = createHBox();
        VBox leftSection = createVBox();
        VBox rightSection = createVBox();
        receiveBestPlayersFromServer(leftSection);
        VBox statsVBox = createVBox();
        receiveStatsFromServer(statsVBox);
        VBox matchHistoryOrganizer = createVBox();
        receiveMatchHistoryFromServer(matchHistoryOrganizer);
        rightSection.getChildren().addAll(statsVBox, matchHistoryOrganizer);
        topSection.getChildren().addAll(leftSection, rightSection);
        overallOrganizer.getChildren().addAll(topSection, backButtonBox);
        BorderPane manager = createManager(overallOrganizer);
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
