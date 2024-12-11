package com.example.javaonlineproject;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;

import static javafx.scene.paint.Color.WHITE;

public class WaitList {
    private Runnable onPlay;
    private Runnable onBack;
    private Runnable onDisconnect;
    private UserInfo user;
    private String[] enemyList;
    private Thread listeningThread;
    private final ListView <String> iteractiveList = new ListView<>();
    private final String[] usedSymbols = new String[2];

    private void initEnemyList() {
        user.getUserOutput().sendMessage("GETENEMY");
        String[] list = user.getUserInput().receiveMessage().split(",");
        populateEnemyList(list);
    }
    private void populateEnemyList(String[] readList) {
        enemyList = Arrays.copyOfRange(readList, 1, readList.length);
    }
    private Text createText(){
        Text label = new Text("Choose your oponent:");
        label.setFont(new Font(32));
        label.setFill(WHITE);
        return label;
    }
    private void refreshList() {
        if (enemyList != null) {
            iteractiveList.getItems().clear();
            for (String username : enemyList)
                iteractiveList.getItems().add(username);
        }
    }
    private Button createInviteButton() {
        Button loginButton = new Button("Invite");
        loginButton.getStyleClass().add("button");
        loginButton.setFont(new Font(16.0));
        loginButton.setOnAction(_ -> inviteButtonFunc());
        return loginButton;
    }
    private Button createBackButton() {
        Button loginButton = new Button("Back");
        loginButton.getStyleClass().add("button");
        loginButton.setFont(new Font(16.0));
        loginButton.setOnAction(_ -> backButtonFunc());
        return loginButton;
    }
    private HBox createHBox() {
        HBox organizer = new HBox(12);
        organizer.setAlignment(Pos.CENTER);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        return organizer;
    }
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setPrefSize(340, 150);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        organizer.setAlignment(Pos.CENTER);
        return organizer;
    }
    private BorderPane createManager(VBox organizer) {
        BorderPane root = new BorderPane(organizer);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    private void manageScene(Stage primaryStage, BorderPane manager) {
        Scene scene = new Scene(manager, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Enemy select");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void start(Stage primaryStage, UserInfo user) {
        this.user = user;
        initEnemyList();
        Text choiceText = createText();
        iteractiveList.setMinSize(200, 200);
        Button invite = createInviteButton();
        Button back = createBackButton();
        HBox buttons = createHBox();
        buttons.getChildren().addAll(invite, back);
        VBox organizer = createVBox();
        organizer.getChildren().addAll(choiceText, iteractiveList, buttons);
        BorderPane manager = createManager(organizer);
        manageScene(primaryStage, manager);
        refreshList();
        listeningForRefresh();
    }
    private void listeningForRefresh() {
        Runnable listener = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                String move = user.getUserInput().receiveMessage();
                if (move == null) continue;
                String[] moveSplit = move.split(",");
                    switch (moveSplit[0]) {
                        case "SOCKETERROR":
                            Platform.runLater(WaitList.this::disconnect);
                            return;
                        case "REFRESH":
                            populateEnemyList(moveSplit);
                            Platform.runLater(WaitList.this::refreshList);
                            break;
                        case "INVITED":
                            String enemyNick = moveSplit[1];
                            for(int i = 0; i < enemyList.length; i++)
                                if (enemyList[i].equals(enemyNick)) {
                                    enemyList[i] = enemyList[i].concat(" - INVITED YOU TO A MATCH");
                                    break;
                                }
                            Platform.runLater(WaitList.this::refreshList);
                            break;
                        case "MATCH":
                            usedSymbols[0] = moveSplit[1];
                            usedSymbols[1] = moveSplit[2];
                            Platform.runLater(WaitList.this::proceedToGame);
                            return;
                        default:
                            break;
                    }
                }
        };
        listeningThread = new Thread(listener);
        listeningThread.setDaemon(true);
        listeningThread.start();
    }

    private void inviteButtonFunc() {
        String enemySignature = String.valueOf(iteractiveList.getSelectionModel().getSelectedItem());
        if (enemySignature.equals("null")) return;
        String[] enemyInfo = enemySignature.split(" ", 2);
        if (enemyInfo.length == 1) {
            user.getUserOutput().sendMessage("INVITE," + enemyInfo[0]);
            for(int i = 0; i < enemyList.length; i++)
                if (enemyList[i].equals(enemyInfo[0])) {
                    enemyList[i] = enemyList[i].concat(" - INVITED THEM TO A MATCH");
                    refreshList();
                    break;
                }
        } else if (!enemyInfo[1].equals("- INVITED THEM TO A MATCH")) user.getUserOutput().sendMessage("PLAY," + enemyInfo[0]);
    }
    private void backButtonFunc(){
        listeningThread.interrupt();
        try {
            listeningThread.join();
        } catch (InterruptedException _) {

        }
        user.getUserOutput().sendMessage("REMOVE");
        onBack.run();
    }
    private void proceedToGame() {
        listeningThread.interrupt();
        try {
            listeningThread.join();
        } catch (InterruptedException _) {

        }
        onPlay.run();
    }
    private void disconnect() {
        listeningThread.interrupt();
        try {
            listeningThread.join();
        } catch (InterruptedException _) {}
        user.closeConnection();
        onDisconnect.run();
    }
    public void setOnPlay(Runnable onPlay) {
        this.onPlay = onPlay;
    }
    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
    public void setOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect = onDisconnect;
    }
    public String[] getSymbols() {
        return usedSymbols;
    }
}
