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

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Klasa odpowiedzialna za zarządzanie statystykami użytkownika, historią meczów oraz najlepszymi graczami.
 * Zawiera interfejs do wyświetlania danych o statystykach i historii meczów.
 */
public class Stats {
    /**
     * Funkcja uruchamiana po powrocie do poprzedniego ekranu
     */
    private Runnable onBack;
    /**
     * Funkcja uruchamiana przy rozłaczeniu
     */
    private Runnable onDisconnect;
    /**
     * Obiekt z informacyjami dotyczącymi użytkownika
     */
    private UserInfo user;
    /**
     * Wątek badający czy nastąpiło rozłączenie
     */
    private Thread disconnectThread;
    /**
     * Obiekt odpowiedzialny za odczytywanie danych
     */
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Główne okno programu
     */
    private Stage primaryStage;
    /**
     * Tworzy przycisk "Back" do powrotu do poprzedniego widoku.
     *
     * @return Przycisk "Back".
     */
    private Button createBackButton() {
        Button backButton = new Button("Back");
        backButton.setFont(new Font(32.0));
        backButton.getStyleClass().add("button");
        backButton.setOnAction(_ -> backButton());
        return backButton;
    }
    /**
     * Tworzy HBox - pojemnik na elementy .
     *
     * @return HBox z ustawionymi odstępami.
     */
    private HBox createHBox() {
        HBox organizer = new HBox(12);
        organizer.setAlignment(Pos.CENTER);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        return organizer;
    }
    /**
     * Tworzy VBox - pojemnik na elementy.
     *
     * @return VBox z ustawionymi odstępami.
     */
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setAlignment(Pos.CENTER);
        organizer.setPadding(new Insets(4, 8, 40, 2));
        return organizer;
    }
    /**
     * Tworzy główny layout w postaci BorderPane, który zawiera całą stronę zorganizowaną w VBox.
     *
     * @param overallOrganizer VBox zawierający wszystkie elementy.
     * @return BorderPane z głównym układem.
     */
    private BorderPane createManager(VBox overallOrganizer) {
        BorderPane root = new BorderPane(overallOrganizer);
        root.setPrefSize(1100, 600); // Wymiary ekranu Stats
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    /**
     * Zarządza sceną JavaFX, ustawia odpowiednią scenę i tytuł okna.
     *
     * @param manager Root BorderPane zawierający układ strony.
     */
    private void manageScene(BorderPane manager) {
        Scene scene = new Scene(manager);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Stats");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * Odbiera historię meczów użytkownika od serwera i wyświetla ją w tabeli.
     *
     * @param organizer VBox, w którym wyświetlane są dane.
     */
    private void receiveMatchHistoryFromServer(VBox organizer) {
        user.getUserOutput().sendMessage("GETMATCHHISTORY");
        String message = user.getUserInput().receiveMessage();
        if (message.startsWith("MATCHHISTORY: ")) {
            String matchHistoryJson = message.substring("MATCHHISTORY: ".length());
            try {
                List<MatchHistoryData> matchHistory = objectMapper.readValue(matchHistoryJson, new TypeReference<>() {});
                displayMatchHistory(matchHistory, organizer);
            } catch (IOException e) {
                System.err.println("Error parsing match history: " + e.getMessage());
                organizer.getChildren().add(new Label("Match history parse ERROR"));
            }
        } else {
            System.out.println("Message does not contain match history: " + message);
            displayMatchHistory(Collections.emptyList(), organizer);
        }
    }
    /**
     * Wyświetla historię meczów w tabeli.
     *
     * @param matchHistory Lista danych historii meczów.
     * @param organizer VBox, w którym wyświetlane są dane.
     */
    private void displayMatchHistory(List<MatchHistoryData> matchHistory, VBox organizer) {
        organizer.getChildren().clear();
        Label headerLabel = new Label("Player match history");
        headerLabel.setFont(new Font(24));
        headerLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        headerLabel.setAlignment(Pos.CENTER);
        organizer.getChildren().add(headerLabel);
        TableView<MatchHistoryData> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: white;");
        TableColumn<MatchHistoryData, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        dateColumn.setPrefWidth(300);
        TableColumn<MatchHistoryData, String> enemyColumn = new TableColumn<>("Enemy");
        enemyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlayer2username()));
        TableColumn<MatchHistoryData, String> resultColumn = new TableColumn<>("Result");
        resultColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getResult()));
        tableView.getColumns().addAll(dateColumn, enemyColumn, resultColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        if (matchHistory == null || matchHistory.isEmpty()) {
            MatchHistoryData placeholder = new MatchHistoryData("No match history found", "", "", "");
            tableView.setItems(FXCollections.observableArrayList(placeholder));
        } else {
            tableView.setItems(FXCollections.observableArrayList(matchHistory));
            dateColumn.setSortType(TableColumn.SortType.DESCENDING);
            tableView.getSortOrder().add(dateColumn);
        }
        organizer.getChildren().add(tableView);
    }
    /**
     * Odbiera statystyki użytkownika od serwera i wyświetla je na ekranie.
     *
     * @param organizer VBox, w którym wyświetlane są dane.
     */
    private void receiveStatsFromServer(VBox organizer) {
        user.getUserOutput().sendMessage("GETSTATS");
        String message = user.getUserInput().receiveMessage();
        if (message.startsWith("STATS:")) {
            String statsJson = message.substring("STATS:".length());
            try {
                StatsData statsData = objectMapper.readValue(statsJson, StatsData.class);
                displayStats(Collections.singletonList(statsData), organizer);
            } catch (IOException e) {
                System.err.println("Error parsing stats: " + e.getMessage());
                organizer.getChildren().add(new Label("Stats parse ERROR"));
            }
        } else {
            System.out.println("Message does not contain stats: " + message);
            organizer.getChildren().add(new Label("No stats found."));
        }
    }
    /**
     * Wyświetla statystyki użytkownika w prostym formacie tekstowym.
     *
     * @param statsData Lista zawierająca dane o statystykach użytkownika.
     * @param organizer VBox, w którym wyświetlane są dane.
     */
    private void displayStats(List<StatsData> statsData, VBox organizer) {
        organizer.getChildren().clear();
        if (statsData == null || statsData.isEmpty()) {
            organizer.getChildren().add(new Label("No stats found."));
        } else {
            for (StatsData stats : statsData) {
                String statsDetails = "You: " + stats.getWins() + "-" + stats.getDraws() + "-" + stats.getLosses();
                Label statsLabel = new Label(statsDetails);
                statsLabel.setFont(new Font(34));
                statsLabel.setTextFill(javafx.scene.paint.Color.RED);
                statsLabel.setAlignment(Pos.CENTER);
                organizer.getChildren().add(statsLabel);
            }
        }
    }
    /**
     * Odbiera dane o najlepszych graczach od serwera i wyświetla je w tabeli.
     *
     * @param organizer VBox, w którym wyświetlane są dane.
     */
    private void receiveBestPlayersFromServer(VBox organizer) {
        user.getUserOutput().sendMessage("GETBESTPLAYERS");
        String message = user.getUserInput().receiveMessage();
        if (message.startsWith("BESTPLAYERS:")) {
            String statsJson = message.substring("BESTPLAYERS:".length());
            try {
                if (!statsJson.startsWith("["))
                    statsJson = "[" + statsJson + "]";
                List<StatsData> bestPlayers = objectMapper.readValue(statsJson, new TypeReference<List<StatsData>>() {});
                displayBestPlayers(bestPlayers, organizer);
            } catch (IOException e) {
                System.err.println("Error parsing best players: " + e.getMessage());
            }
        } else {
            System.out.println("Message does not contain best players: " + message);
        }
    }
    /**
     * Wyświetla tabelę najlepszych graczy.
     *
     * @param bestPlayers Lista danych o najlepszych graczach.
     * @param organizer VBox, w którym wyświetlane są dane.
     */
    private void displayBestPlayers(List<StatsData> bestPlayers, VBox organizer) {
        organizer.getChildren().clear();
        Label headerLabel = new Label("Top 10 players");
        headerLabel.setFont(new Font(24));
        headerLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        headerLabel.setAlignment(Pos.CENTER);
        organizer.getChildren().add(headerLabel);
        TableView<StatsData> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: white;");
        TableColumn<StatsData, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        TableColumn<StatsData, Integer> winsColumn = new TableColumn<>("Wins");
        winsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getWins()).asObject());
        winsColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Integer wins, boolean empty) {
                super.updateItem(wins, empty);
                if (empty || wins == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(wins));
                    setStyle("-fx-background-color: #90EE90;");
                }
            }
        });
        TableColumn<StatsData, Integer> drawsColumn = new TableColumn<>("Draws");
        drawsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDraws()).asObject());
        drawsColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Integer draws, boolean empty) {
                super.updateItem(draws, empty);
                if (empty || draws == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(draws));
                    setStyle("-fx-background-color: #D3D3D3;");
                }
            }
        });
        TableColumn<StatsData, Integer> lossesColumn = new TableColumn<>("Losses");
        lossesColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLosses()).asObject());
        lossesColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Integer losses, boolean empty) {
                super.updateItem(losses, empty);
                if (empty || losses == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(losses));
                    setStyle("-fx-background-color: #FF7F7F;");
                }
            }
        });
        tableView.getColumns().addAll(usernameColumn, winsColumn, drawsColumn, lossesColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        if (bestPlayers == null || bestPlayers.isEmpty())
            organizer.getChildren().add(new Label("No best players found."));
        else
            tableView.setItems(FXCollections.observableArrayList(bestPlayers));
        organizer.getChildren().add(tableView);
    }
    /**
     * Rozpoczyna działanie okna statystyk.
     *
     * @param primaryStage Główne okno aplikacji.
     * @param user Obiekt reprezentujący użytkownika, którego statystyki mają być wyświetlane.
     */
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
        HBox tablesBox = new HBox(12);
        receiveBestPlayersFromServer(leftSection);
        receiveMatchHistoryFromServer(rightSection);
        tablesBox.getChildren().addAll(leftSection, rightSection);
        VBox statsVBox = createVBox();
        receiveStatsFromServer(statsVBox);
        topSection.getChildren().addAll(tablesBox, statsVBox);
        overallOrganizer.getChildren().addAll(topSection, backButtonBox);
        BorderPane manager = createManager(overallOrganizer);
        manageScene(manager);
        checkForDisconnect();
    }
    /**
     * Sprawdza, czy połączenie zostało rozłączone przez serwer i jeśli tak to wykonuje rozłączenie.
     */
    private void checkForDisconnect() {
        Runnable disconnectChecker = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                String move = user.getUserInput().receiveMessage();
                if (move == null || move.equals("SOCKETERROR") || move.equals("CLOSING")) {
                    Platform.runLater(this::disconnect);
                    return;
                }
            }
        };
        disconnectThread = new Thread(disconnectChecker);
        disconnectThread.setDaemon(true);
        disconnectThread.start();
    }
    /**
     * Obsługuje kliknięcie przycisku "Back"; kończy połączenie i wraca do poprzedniego widoku.
     */
    private void backButton() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException ignored) {}
        onBack.run();
    }
    /**
     * Zamyka połączenie użytkownika i wykonuje akcję rozłączenia.
     */
    private void disconnect() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException ignored) {}
        user.closeConnection();
        onDisconnect.run();
    }
    /**
     * Ustawia funkcję, która ma być wywołana po naciśnięciu przycisku "Back".
     *
     * @param onBack Funkcja, która ma zostać wywołana po naciśnięciu przycisku "Back".
     */
    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
    /**
     * Ustawia funkcję, która ma być wywołana po rozłączeniu użytkownika.
     *
     * @param onDisconnect Funkcja, która ma zostać wywołana po rozłączeniu.
     */
    public void setOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect = onDisconnect;
    }
}




