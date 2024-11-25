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
    private UserInfo user;
    private String[] enemyList;
    private Thread listeningThread;
    private final ListView <String> iteractiveList = new ListView<>();

    private void populateEnemyList() {
        String list;
        list = user.getUserInput().receiveMessage();
        if (list == null){
            return;
        }
        String[]data = list.split(",");
        enemyList = Arrays.copyOfRange(data, 1, data.length);
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
                for (String username : enemyList) {
                    iteractiveList.getItems().add(username);
                    }
        }
    }
    private Button createPlayButton() {
        Button loginButton = new Button("Invite");
        loginButton.setFont(new Font(16.0));
        loginButton.setOnAction(_ -> playButtonFunc());
        return loginButton;
    }
    private Button createBackButton() {
        Button loginButton = new Button("Back");
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
        Scene scene = new Scene(manager);
        primaryStage.setTitle("ModeSelect");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void start(Stage primaryStage, UserInfo user) {
        this.user = user;
        user.getUserOutput().sendMessage("GETENEMY");
        populateEnemyList();
        Text choiceText = createText();
        refreshList();
        iteractiveList.setMinSize(200, 200);
        Button invite = createPlayButton();
        Button back = createBackButton();
        HBox buttons = createHBox();
        buttons.getChildren().addAll(invite, back);
        VBox organizer = createVBox();
        organizer.getChildren().addAll(choiceText, iteractiveList, buttons);
        BorderPane manager = createManager(organizer);
        manageScene(primaryStage, manager);
        listeningForRefresh();
    }
    private void listeningForRefresh() {
        Runnable listener = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                populateEnemyList();
                Platform.runLater(WaitList.this::refreshList);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException _) {
                    return;
                }
            }
        };
        listeningThread = new Thread(listener);
        listeningThread.setDaemon(true);
        listeningThread.start();
    }

    public void playButtonFunc() {
        listeningThread.interrupt();
    }
    public void backButtonFunc(){
        listeningThread.interrupt();
        user.getUserOutput().sendMessage("REMOVE");
        onBack.run();
    }
    public void setOnPlay(Runnable onPlay) {
        this.onPlay = onPlay;
    }
    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
}
