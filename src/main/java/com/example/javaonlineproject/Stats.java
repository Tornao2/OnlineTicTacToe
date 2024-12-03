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
    import java.util.Collections;
    import java.util.List;
    /*
    Wszystkie parametry są wysyłane do stats, zostało do zrobienia wyświetlanie ich wszystkich i żeby wyglądało to jakoś
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
            organizer.setAlignment(Pos.CENTER_RIGHT);
            return organizer;
        }
    
        private BorderPane createManager(VBox organizer, Button backButton) {
            BorderPane root = new BorderPane();
            root.setCenter(organizer);
            root.setBottom(backButton);
            BorderPane.setAlignment(backButton, Pos.BOTTOM_CENTER);
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
                    matchLabel.setAlignment(Pos.CENTER_RIGHT);
                    organizer.getChildren().add(matchLabel);
                }
            }
            Button backButton = createBackButton();
            BorderPane root = createManager(organizer, backButton);
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
                        System.out.println(matchHistoryJson);//DEBUG
                        try {
                            List<MatchHistoryData> matchHistory = objectMapper.readValue(matchHistoryJson, new TypeReference<List<MatchHistoryData>>() {});
                            Platform.runLater(() -> displayMatchHistory(matchHistory));
                            break;
                        } catch (IOException e) {
                            System.err.println("Error parsing match history: " + e.getMessage());
                            break;
                        }
                    } else {
                        System.out.println("Message does not contain match history: " + message);
                        break;
                    }
                }
            };
    
            Thread historyThread = new Thread(matchHistoryReceiver);
            historyThread.setDaemon(true);
            historyThread.start();
        }
    
        private void reciveStatsFromServer(){
            while (!Thread.currentThread().isInterrupted()) {
                /*
                musisz zamienic to co zakomentowane w zaleznosci co chcesz wyswietlic
                narazie nie jest zrobione
                 */
                user.getUserOutput().sendMessage("GETBESTPLAYERS"); //DEBUG //("GETSTATS");
                String message = user.getUserInput().receiveMessage();
                if(message == null) continue;
                if(message.startsWith("Top Player: ")){ //DEBUG //("STATS:")
                    String statsJson = message.substring("STATS:".length());
                    System.out.println(statsJson); //debug
                    try{
                        StatsData statsData = objectMapper.readValue(statsJson, StatsData.class);
                        Platform.runLater(() -> displayStats(Collections.singletonList(statsData)));
                        break;
                    }catch(IOException e){
                        System.err.println("Error Parsing stats: " + e.getMessage());
                        break;
                    }
                }else{
                    System.out.println("Message does not contain statsData " + message);
                    break;
                }
            }
        }
    
        private void displayStats(List<StatsData> statsData) {
            VBox organizer = createVBox();
            if(statsData == null || statsData.isEmpty()) organizer.getChildren().add((new Label("No stats found.")));
            else{
                for(StatsData stats : statsData){
                    String statsDetails = "Username: " + stats.getUsername() +
                            " |Wins: " + stats.getWins() +
                            " |Draws" + stats.getDraws() +
                            " |Loses" + stats.getLosses();
                    Label statsLabel = new Label(statsDetails);
                    statsLabel.setFont(new Font(14));
                    statsLabel.setTextFill(Color.RED);
                    organizer.getChildren().add(statsLabel);
                }
            }
    
            Button backButton = createBackButton();
            BorderPane root = createManager(organizer, backButton);
            manageScene(primaryStage, root);
        }
    
    
        public void start(Stage primaryStage, UserInfo user) {
            this.user = user;
            this.primaryStage = primaryStage;
    
            Button backButton = createBackButton();
            VBox organizer = createVBox();
            organizer.getChildren().add(backButton);
            BorderPane manager = createManager(organizer, backButton);
            reciveStatsFromServer();
         //   reciveMatchHistoryFromServer(); jest zakomentowane jesli chcesz wyswietlic match history
            //obecnie nie ma od wyswietlania bialsnu top 3 graczy mozna zmienic na wiecej

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
