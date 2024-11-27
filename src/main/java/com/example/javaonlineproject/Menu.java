package com.example.javaonlineproject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static javafx.scene.paint.Color.WHITE;

public class Menu  {
    private Thread probeChecker;
    private Runnable onStartSuccess;
    private UserInfo user;

    private Text createWelcomeText() {
        Text text = new Text("Welcome " + user.getUsername() + "!");
        text.setFill(WHITE);
        text.setFont(new Font(16));
        return text;
    }
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
        primaryStage.setTitle("Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
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
        probeLogic();
    }

    private void probeLogic() {
        Runnable probeListener = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                String probeCheck = user.getUserInput().receiveMessage();
                if (probeCheck == null){
                    continue;
                }
                 if (probeCheck.equals("PROBED")){
                    user.getUserOutput().sendMessage("PROBED");
                 }
            }
        };
        probeChecker = new Thread(probeListener);
        probeChecker.setDaemon(true);
        probeChecker.start();
    }
    private void ChangeScene() {
        probeChecker.interrupt();
        try {
            probeChecker.join();
        } catch(InterruptedException _) {

        }
        onStartSuccess.run();
    }
    private void statsButton() {
        probeChecker.interrupt();
        try {
            probeChecker.join();
        } catch(InterruptedException _) {

        }
        //Do implementacji
        //Przy wykonaniu stwórz wiadomość do serwera o aktualizacje danych
        //Trzeba zaimplementować aby serwer w pliku zapisywał dane wszystkich graczy
        //I wyświetl w jakim formacie potrzeba
    }
    private void quitButton() {
        probeChecker.interrupt();
        try {
            probeChecker.join();
        } catch(InterruptedException _) {

        }
        user.closeConnection();
        System.exit(0);
    }

    public void setOnStartSuccess(Runnable onLoginSuccess) {
        this.onStartSuccess = onLoginSuccess;
    }
}