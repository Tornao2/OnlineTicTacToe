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

/**
 * Klasa reprezentująca menu aplikacji.
 * Odpowiada za tworzenie interfejsu użytkownika z przyciskami do rozpoczęcia gry,
 * wyświetlania statystyk oraz zamknięcia aplikacji.
 */
public class Menu {
    /**
     * Wątek odpowiedzialny za sprawdzanie rozłączania
     */
    private Thread disconnectThread;
    /**
     * Funkcja wykonywana jeśli gracz chce rozpocząć rozgrywkę
     */
    private Runnable onStartSuccess;
    /**
     * Funkcja wykonywana jeśli gracz chce zobaczyć statystyki
     */
    private Runnable onStats;
    /**
     * Obiekt gracza
     */
    private UserInfo user;
    /**
     * Tworzy tekst powitalny z nazwą użytkownika.
     * @return Tekst powitalny.
     */
    private Text createWelcomeText() {
        Text text = new Text("Welcome " + user.getUsername() + "!");
        text.getStyleClass().add("welcome-text");
        text.setFill(javafx.scene.paint.Color.WHITE);
        return text;
    }
    /**
     * Tworzy przycisk "Start" i ustawia akcję na zmianę sceny.
     * @return Przycisk "Start".
     */
    private Button createStartButton() {
        Button startButton = new Button("Start");
        startButton.setFont(new Font(32.0));
        startButton.getStyleClass().add("button");
        startButton.setPrefSize(250,80);
        startButton.setOnAction(_ -> ChangeScene());
        return startButton;
    }
    /**
     * Tworzy przycisk "Stats" i ustawia akcję na wyświetlanie statystyk.
     * @return Przycisk "Stats".
     */
    private Button createStatsButton() {
        Button statsButton = new Button("Stats");
        statsButton.setFont(new Font(32.0));
        statsButton.getStyleClass().add("button");
        statsButton.setPrefSize(250,80);
        statsButton.setOnAction(_ -> statsButton());
        return statsButton;
    }
    /**
     * Tworzy przycisk "Quit" i ustawia akcję na zakończenie aplikacji.
     * @return Przycisk "Quit".
     */
    private Button createQuitButton() {
        Button quitButton = new Button("Quit");
        quitButton.setFont(new Font(32.0));
        quitButton.getStyleClass().add("button");
        quitButton.setPrefSize(250,80);
        quitButton.setOnAction(_ -> quitButton());
        return quitButton;
    }
    /**
     * Tworzy kontener typu VBox do organizowania elementów interfejsu.
     * @return Kontener typu VBox.
     */
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setPrefSize(280, 210);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        organizer.setAlignment(Pos.BASELINE_CENTER);
        return organizer;
    }
    /**
     * Tworzy główny kontener typu BorderPane, który zarządza rozmieszczeniem elementów na scenie.
     * @param organizer Kontener zawierający elementy interfejsu.
     * @return Główny kontener BorderPane.
     */
    private BorderPane createManager(VBox organizer) {
        BorderPane root = new BorderPane(organizer);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    /**
     * Zarządza ustawieniem sceny i wyświetleniem interfejsu użytkownika.
     * @param primaryStage Okno aplikacji.
     * @param manager Główny kontener aplikacji.
     */
    private void manageScene(Stage primaryStage, BorderPane manager) {
        Scene scene = new Scene(manager, 400, 400);
        primaryStage.setTitle("Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }
    /**
     * Inicjuje menu aplikacji, ustawia wszystkie elementy interfejsu oraz sprawdza połączenie użytkownika.
     * @param primaryStage Okno aplikacji.
     * @param user Obiekt reprezentujący użytkownika.
     */
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
    /**
     * Sprawdza stan połączenia użytkownika w tle i przerywa połączenie, jeśli wystąpił błąd.
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
     * Zmienia scenę po naciśnięciu przycisku "Start".
     */
    private void ChangeScene() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException _) {}
        onStartSuccess.run();
    }
    /**
     * Wyświetla statystyki po naciśnięciu przycisku "Stats".
     */
    private void statsButton() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException _) {}
        onStats.run();
    }
    /**
     * Kończy połączenie i zamyka aplikację po naciśnięciu przycisku "Quit".
     */
    private void quitButton() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException _) {}
        user.getUserOutput().sendMessage("SOCKETERROR");
        user.closeConnection();
        System.exit(0);
    }
    /**
     * Zamyka połączenie i kończy aplikację.
     */
    private void disconnect() {
        disconnectThread.interrupt();
        try {
            disconnectThread.join();
        } catch (InterruptedException _) {}
        user.closeConnection();
        System.exit(-2);
    }
    /**
     * Ustawia akcję do wykonania po pomyślnym rozpoczęciu gry.
     * @param onLoginSuccess Akcja do wykonania po rozpoczęciu gry.
     */
    public void setOnStartSuccess(Runnable onLoginSuccess) {
        this.onStartSuccess = onLoginSuccess;
    }
    /**
     * Ustawia akcję do wykonania po wyświetleniu statystyk.
     * @param onStats Akcja do wykonania po wyświetleniu statystyk.
     */
    public void setOnStats(Runnable onStats) {
        this.onStats = onStats;
    }
}



