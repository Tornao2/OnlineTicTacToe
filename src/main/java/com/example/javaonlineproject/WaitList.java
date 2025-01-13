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

/**
 * Klasa reprezentująca ekran, w którym użytkownik może wybrać przeciwnika do gry.
 * Odpowiada za zarządzanie listą dostępnych przeciwników, zapraszanie ich do gry
 * oraz reagowanie na zmiany w stanie listy przeciwników.
 */
public class WaitList {
    /**
     * Funkcja uruchamiana po rozpoczęciu gry.
     */
    private Runnable onPlay;
    /**
     * Funkcja uruchamiana po powrocie do poprzedniego ekranu.
     */
    private Runnable onBack;
    /**
     * Informacje o użytkowniku.
     */
    private UserInfo user;
    /**
     * Lista przeciwników.
     */
    private String[] enemyList;
    /**
     * Wątek nasłuchujący na zmiany w liście przeciwników.
     */
    private Thread listeningThread;
    /**
     * Interaktywna lista przeciwników.
     */
    private final ListView<String> iteractiveList = new ListView<>();
    /**
     * Przechowuje użyte symbole ("X" i "O").
     */
    private final String[] usedSymbols = new String[2];
    /**
     * Inicjalizuje listę przeciwników, wysyłając zapytanie o dostępnych graczy.
     */
    private void initEnemyList() {
        user.getUserOutput().sendMessage("GETENEMY");
        String[] list = user.getUserInput().receiveMessage().split(",");
        populateEnemyList(list);
    }
    /**
     * Uzupełnia listę przeciwników na podstawie otrzymanej tablicy nazw użytkowników.
     *
     * @param readList Lista przeciwników w formie tablicy String.
     */
    private void populateEnemyList(String[] readList) {
        enemyList = Arrays.copyOfRange(readList, 1, readList.length);
    }
    /**
     * Tworzy tekstowy nagłówek "Wybierz przeciwnika".
     *
     * @return Tworzony obiekt Text.
     */
    private Text createText() {
        Text label = new Text("Choose your oponent:");
        label.setFont(new Font(32));
        label.setFill(WHITE);
        return label;
    }
    /**
     * Odświeża listę przeciwników wyświetlaną w interfejsie użytkownika.
     */
    private void refreshList() {
        if (enemyList != null) {
            iteractiveList.getItems().clear();
            for (String username : enemyList)
                iteractiveList.getItems().add(username);
        }
    }
    /**
     * Tworzy przycisk "Invite", który wysyła zaproszenie do przeciwnika.
     *
     * @return Przycisk "Invite".
     */
    private Button createInviteButton() {
        Button loginButton = new Button("Invite");
        loginButton.getStyleClass().add("button");
        loginButton.setFont(new Font(16.0));
        loginButton.setOnAction(_ -> inviteButtonFunc());
        return loginButton;
    }
    /**
     * Tworzy przycisk "Back", który wraca do poprzedniego ekranu.
     *
     * @return Przycisk "Back".
     */
    private Button createBackButton() {
        Button loginButton = new Button("Back");
        loginButton.getStyleClass().add("button");
        loginButton.setFont(new Font(16.0));
        loginButton.setOnAction(_ -> backButtonFunc());
        return loginButton;
    }
    /**
     * Tworzy HBox - kontener na przyciski z odpowiednim odstępem.
     *
     * @return Utworzony obiekt HBox.
     */
    private HBox createHBox() {
        HBox organizer = new HBox(12);
        organizer.setAlignment(Pos.CENTER);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        return organizer;
    }
    /**
     * Tworzy VBox, który przechowuje listę przeciwników i przyciski.
     *
     * @return Utworzony obiekt VBox.
     */
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setPrefSize(340, 150);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        organizer.setAlignment(Pos.CENTER);
        return organizer;
    }
    /**
     * Tworzy główny kontener BorderPane z organizacją elementów w środku.
     *
     * @param organizer Kontener VBox z elementami.
     * @return Główny kontener BorderPane.
     */
    private BorderPane createManager(VBox organizer) {
        BorderPane root = new BorderPane(organizer);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    /**
     * Zarządza ustawieniem sceny, wyświetlaniem okna aplikacji.
     *
     * @param primaryStage Główne okno aplikacji.
     * @param manager Główny kontener aplikacji.
     */
    private void manageScene(Stage primaryStage, BorderPane manager) {
        Scene scene = new Scene(manager, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Enemy select");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * Inicjuje scenę z ekranem wyboru przeciwnika.
     *
     * @param primaryStage Główne okno aplikacji.
     * @param user Obiekt zawierający dane użytkownika.
     */
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
    /**
     * Uruchamia nasłuch na zmiany w stanie listy przeciwników.
     */
    private void listeningForRefresh() {
        Runnable listener = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                String move = user.getUserInput().receiveMessage();
                if (move == null) {
                    Platform.runLater(WaitList.this::disconnect);
                    return;
                }
                String[] moveSplit = move.split(",");
                switch (moveSplit[0]) {
                    case "CLOSING":
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
    /**
     * Funkcja wywoływana po naciśnięciu przycisku "Invite". Wysyła zaproszenie do wybranego przeciwnika.
     */
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
    /**
     * Funkcja wywoływana po naciśnięciu przycisku "Back". Zamyka nasłuch i wraca do poprzedniego ekranu.
     */
    private void backButtonFunc(){
        listeningThread.interrupt();
        try {
            listeningThread.join();
        } catch (InterruptedException _) {}
        user.getUserOutput().sendMessage("REMOVE");
        onBack.run();
    }
    /**
     * Funkcja, która wywoływana jest, gdy rozpoczyna się gra z przeciwnikiem.
     */
    private void proceedToGame() {
        listeningThread.interrupt();
        try {
            listeningThread.join();
        } catch (InterruptedException _) {}
        onPlay.run();
    }
    /**
     * Funkcja rozłączająca użytkownika w przypadku błędu lub zamknięcia aplikacji.
     */
    private void disconnect() {
        listeningThread.interrupt();
        try {
            listeningThread.join();
        } catch (InterruptedException _) {}
        user.closeConnection();
        System.exit(-2);
    }
    /**
     * Ustawia funkcję, która ma zostać wywołana po rozpoczęciu gry.
     *
     * @param onPlay Funkcja do wywołania po rozpoczęciu gry.
     */
    public void setOnPlay(Runnable onPlay) {
        this.onPlay = onPlay;
    }

    /**
     * Ustawia funkcję, która ma zostać wywołana po powrocie do poprzedniego ekranu.
     *
     * @param onBack Funkcja do wywołania po powrocie do poprzedniego ekranu.
     */
    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
    /**
     * Zwraca symbole używane w grze ("X" i "O").
     *
     * @return Tablica z użytymi symbolami.
     */
    public String[] getSymbols() {
        return usedSymbols;
    }
}












