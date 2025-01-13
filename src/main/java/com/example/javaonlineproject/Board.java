package com.example.javaonlineproject;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import static javafx.scene.paint.Color.WHITE;

/**
 * Klasa reprezentująca logikę i interfejs gry w kółko-krzyżyk.
 * Obsługuje rozgrywkę pomiędzy graczami, obsługę czatu oraz stan gry.
 */
public class Board {
    /**
     * Wątek odpowiedzialny za nasłuchiwanie chatu
     */
    private Thread messageListener;
    /**
     * Funkcja wykonywana po rezygnacji z gry
     */
    private Runnable onResign;
    /**
     * Napis informujący o statusie meczu
     */
    private Text statusText = new Text();
    /**
     * Napis informujący o wyniku meczu
     */
    private Text scoreText = new Text();
    /**
     * Tablica przycisków służąca jako plansza
     */
    private final Button[][] board = new Button[3][3];
    /**
     * Użyte symbole (X i O)
     */
    private String[] symbolUsed;
    /**
     * Czy gracz się poruszył
     */
    private Boolean moved;
    /**
     * Czy gracz zrezygnował
     */
    private Boolean quiting = false;
    /**
     * Czy druga strona chce rematchu
     */
    private boolean otherSideRematch = false;
    /**
     * Czy mecz został skończony
     */
    private boolean finishedMatch = false;
    /**
     * Wygrane w tej sesji
     */
    private int thisSessionW = 0;
    /**
     * Remisy w tej sesji
     */
    private int thisSessionD = 0;
    /**
     * Przegrane w tej sesji
     */
    private int thisSessionL = 0;
    /**
     * Obiekt użytkownika
     */
    private UserInfo user;
    /**
     * Nazwa przeciwnika
     */
    private String enemyName;
    /**
     * VBox przechowujący elementy chatu
     */
    private final VBox chatView = new VBox(10);
    /**
     * Pole do wpisywania wiadomości przez chat
     */
    private final TextField chatField = new TextField();
    /**
     * Scrollpane dla scrollowania chatu
     */
    private ScrollPane scrollPane;
    /**
     * Tworzy tekst z wynikiem gry.
     * Wysyła zapytanie o nazwę przeciwnika i ustawia wynik gry.
     */
    private void createScoreText() {
        user.getUserOutput().sendMessage("NAME");
        enemyName = user.getUserInput().receiveMessage();
        scoreText = new Text("You 0-0-0 " + enemyName);
        scoreText.setFill(WHITE);
        scoreText.setFont(new Font(26));
    }
    /**
     * Odświeża tekst z wynikiem gry.
     * Aktualizuje wynik na podstawie wyników sesji.
     */
    private void refreshScoreText() {
        scoreText.setText("You " + thisSessionW + "-" + thisSessionD + "-" + thisSessionL + " " + enemyName);
    }
    /**
     * Inicjalizuje tekst statusu - czyja jest kolejka.
     * Ustawia tekst w zależności od tego, czy ruch został wykonany.
     */
    private void initStatusText() {
        if (moved)
            statusText = new Text("Enemy's turn!");
        else
            statusText = new Text("Your turn!");
        statusText.setFill(WHITE);
        statusText.setFont(new Font(26));
        statusText.setWrappingWidth(600);
        statusText.setTextAlignment(TextAlignment.CENTER);
    }
    /**
     * Inicjalizuje planszę gry.
     * Tworzy przyciski do gry w kółko-krzyżyk i przypisuje im odpowiednią funkcję.
     *
     * @return plansza gry w postaci obiektu GridPane
     */
    private GridPane initializeBoard() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_CENTER);
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        gridPane.getStyleClass().add("grid-pane");
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Button cell = new Button("");
                cell.getStyleClass().add("button");
                cell.setMinSize(150, 150);
                cell.setFont(new Font(48));
                final int r = row;
                final int c = column;
                cell.setOnAction(_ -> handleMove(r, c, cell));
                board[r][c] = cell;
                gridPane.add(cell, column, row);
            }
        }
        return gridPane;
    }
    /**
     * Inicjalizuje widok czatu.
     * Pobiera historię czatu i wyświetla ją w odpowiednim kontenerze.
     */
    private void initChatView() {
        user.getUserOutput().sendMessage("NAME");
        enemyName = user.getUserInput().receiveMessage();
        chatField.setPrefWidth(450);
        chatField.getStyleClass().add("text-field");
        chatField.setPrefHeight(40);
        scrollPane = new ScrollPane(chatView);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setPrefSize(600, 600);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPadding(new Insets(10));
        user.getUserOutput().sendMessage("GETCHATHISTORY");
        String receivedMessages = user.getUserInput().receiveMessage();
        if (receivedMessages != null && !receivedMessages.isEmpty()) {
            String[] messagePairs = receivedMessages.split(",");
            for (String pair : messagePairs) {
                String[] senderMessage = pair.split(":");
                if (senderMessage.length == 2) {
                    String sender = senderMessage[0].trim();
                    String message = senderMessage[1].trim().replace("~", ",");
                    Label messageLabel = new Label(message);
                    messageLabel.setFont(new Font(14));
                    messageLabel.setMaxWidth(400);
                    messageLabel.setWrapText(true);
                    messageLabel.setPadding(new Insets(3, 3, 0, 3));
                    HBox messageContainer = new HBox(messageLabel);
                    if (sender.equals(enemyName)) {
                        messageContainer.getStyleClass().add("message-left");
                        messageContainer.setAlignment(Pos.CENTER_LEFT);
                    } else {
                        messageContainer.getStyleClass().add("message-right");
                        messageContainer.setAlignment(Pos.CENTER_RIGHT);
                    }
                    chatView.getChildren().add(messageContainer);
                    scrollPane.setVvalue(1.0);
                }
            }
        }
    }
    /**
     * Inicjalizuje pole do wprowadzania wiadomości w czacie.
     * Ustawia domyślny tekst wskazujący na wprowadzenie wiadomości.
     */
    private void initSendingField() {
        chatField.setFont(new Font(16));
        chatField.getStyleClass().add("text-field");
        chatField.setPromptText("Type your message here 😎...");
    }
    /**
     * Tworzy przycisk do wysyłania wiadomości.
     *
     * @return przycisk "Send"
     */
    private Button sendMessageButton() {
        Button send = new Button("Send");
        send.getStyleClass().add("send-button");
        send.setFont(new Font(16));
        send.setOnAction(_ -> addMessage(false, chatField.getText()));
        return send;
    }
    /**
     * Dodaje wiadomość do czatu.
     * Jeśli wiadomość jest od gracza, wysyła ją do przeciwnika oraz wyświetla na czacie.
     *
     * @param isOpponent flaga określająca, czy wiadomość jest od przeciwnika
     * @param message    wiadomość do dodania
     */
    private void addMessage(boolean isOpponent, String message) {
        message = message.replace(',', '~');
        if (!message.isBlank() && !message.isEmpty()) {
            Label messageLabel = new Label(message.replace("~", ","));
            messageLabel.setFont(new Font(14));
            messageLabel.setMaxWidth(400);
            messageLabel.setWrapText(true);
            messageLabel.setPadding(new Insets(3, 3, 0, 3));
            HBox messageContainer = new HBox(messageLabel);
            if (!isOpponent) {
                messageContainer.getStyleClass().add("message-right");
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                user.getUserOutput().sendMessage("MESSAGE," + message);
                chatField.clear();
            } else {
                messageContainer.getStyleClass().add("message-left");
                messageContainer.setAlignment(Pos.CENTER_LEFT);
            }
            chatView.getChildren().add(messageContainer);
            scrollPane.setVvalue(1.0);
        }
    }
    /**
     * Obsługuje wykonanie ruchu na planszy.
     * Sprawdza, czy ruch jest możliwy, a następnie sprawdza, czy gracz wygrał lub zremisował.
     *
     * @param row   numer wiersza, w którym wykonano ruch
     * @param column numer kolumny, w której wykonano ruch
     * @param cell  reprezentuje pole na planszy
     */
    private void handleMove(int row, int column, Button cell) {
        if (!moved && !finishedMatch && cell.getText().isEmpty()) {
            cell.setText(symbolUsed[0]);
            user.getUserOutput().sendMessage("MOVE," + row + "," + column);
            if (checkWin()) {
                thisSessionW++;
                refreshScoreText();
                user.getUserOutput().sendMessage("WIN");
                statusText.setText("You won!");
                finishedMatch = true;
            } else if (checkDraw()) {
                thisSessionD++;
                refreshScoreText();
                user.getUserOutput().sendMessage("DRAW");
                statusText.setText("You tied!");
                finishedMatch = true;
            } else {
                moved = true;
                statusText.setText("Enemy's turn!");
            }
        }
    }
    /**
     * Sprawdza czy któryś z graczy wygrał.
     *
     * @return true, jeśli któryś z graczy wygrał
     */
    private boolean checkWin() {
        String color = "-fx-background-color: #1e990e";
        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 3; i++) {
                if (board[i][0].getText().equals(symbolUsed[j]) &&
                        board[i][1].getText().equals(symbolUsed[j]) &&
                        board[i][2].getText().equals(symbolUsed[j])) {
                    board[i][0].setStyle(color);
                    board[i][1].setStyle(color);
                    board[i][2].setStyle(color);
                    return true;
                }
            }
            for (int i = 0; i < 3; i++) {
                if (board[0][i].getText().equals(symbolUsed[j]) &&
                        board[1][i].getText().equals(symbolUsed[j]) &&
                        board[2][i].getText().equals(symbolUsed[j])) {
                    board[0][i].setStyle(color);
                    board[1][i].setStyle(color);
                    board[2][i].setStyle(color);
                    return true;
                }
            }
            if (board[0][0].getText().equals(symbolUsed[j]) &&
                    board[1][1].getText().equals(symbolUsed[j]) &&
                    board[2][2].getText().equals(symbolUsed[j])) {
                board[0][0].setStyle(color);
                board[1][1].setStyle(color);
                board[2][2].setStyle(color);
                return true;
            }
            if (board[0][2].getText().equals(symbolUsed[j]) &&
                    board[1][1].getText().equals(symbolUsed[j]) &&
                    board[2][0].getText().equals(symbolUsed[j])) {
                board[0][2].setStyle(color);
                board[1][1].setStyle(color);
                board[2][0].setStyle(color);
                return true;
            }
            color = "-fx-background-color: #9e0a03";
        }
        return false;
    }
    /**
     * Sprawdza czy gra zakończyła się remisem.
     *
     * @return true, jeśli na planszy nie ma już wolnych miejsc
     */
    private boolean checkDraw() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j].getText().isEmpty())
                    return false;
        String color = "-fx-background-color: #a29390 ";
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j].setStyle(color);
        return true;
    }
    /**
     * Resetuje planszę do stanu początkowego.
     * Usuwa wszystkie teksty z pól, a także resetuje status gry.
     */
    private void resetBoard() {
        for (Button[] row : board) {
            for (Button cell : row) {
                cell.setText("");
                cell.setStyle(null);
                cell.getStyleClass().add("button");
            }
        }
        finishedMatch = false;
        otherSideRematch = false;
        setTurns();
    }
    /**
     * Ustawia kolejność ruchów.
     * Określa kto ma teraz wykonać ruch (gracz czy przeciwnik).
     */
    private void setTurns() {
        moved = !symbolUsed[0].equals("X");
        if (moved)
            statusText.setText("Enemy's turn!");
        else
            statusText.setText("Your turn!");
    }
    /**
     * Rozpoczyna grę na oknie głównym.
     * Tworzy wszystkie elementy interfejsu użytkownika i obsługuje logikę gry.
     *
     * @param primaryStage główne okno aplikacji
     * @param user        dane użytkownika
     * @param usedSymbols symbole używane przez graczy
     */
    public void start(Stage primaryStage, UserInfo user, String[] usedSymbols) {
        this.user = user;
        this.symbolUsed = usedSymbols;
        initSendingField();
        setTurns();
        initChatView();
        initStatusText();
        GridPane gameGrid = initializeBoard();
        createScoreText();
        Button resignButton = createResignButton();
        Button rematchButton = createRematchButton();
        Button sendButton = sendMessageButton();
        HBox buttons = createHBox();
        VBox gameOrganizer = createVBox();
        VBox chatOrganizer = createVBox();
        HBox chatSenders = createHBox();
        HBox overallOrganizer = createHBox();
        buttons.getChildren().addAll(rematchButton, resignButton);
        gameOrganizer.getChildren().addAll(statusText, scoreText, gameGrid, buttons);
        chatSenders.getChildren().addAll(chatField, sendButton);
        chatOrganizer.getChildren().addAll(scrollPane, chatSenders);
        overallOrganizer.getChildren().addAll(gameOrganizer, chatOrganizer);
        BorderPane manager = createManager(overallOrganizer);
        manageScene(primaryStage, manager);
        listeningLogic();
    }
    /**
     * Tworzy przycisk umożliwiający rezygnację z gry.
     * Przycisk jest oznaczony jako "Rezygnuj" i wywołuje akcję rezygnacji po kliknięciu.
     *
     * @return Przyciski do rezygnacji z gry.
     */
    private Button createResignButton() {
        Button resign = new Button("Resign");
        resign.setFont(new Font(16));
        resign.setOnAction(_ -> resign());
        return resign;
    }
    /**
     * Tworzy przycisk umożliwiający żądanie dogrywki po zakończonej grze.
     * Przycisk jest oznaczony jako "Remis" i wywołuje akcję dogrywki po kliknięciu.
     *
     * @return Przyciski do żądania remisu.
     */
    private Button createRematchButton() {
        Button rematch = new Button("Rematch");
        rematch.setFont(new Font(16));
        rematch.setOnAction(_ -> rematch());
        return rematch;
    }
    /**
     * Tworzy kontener typu VBox z wypełnieniem i wyrównaniem do środka.
     *
     * @return Kontener VBox.
     */
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setAlignment(Pos.CENTER);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        return organizer;
    }
     /**
     * Tworzy kontener typu HBox z wypełnieniem i wyrównaniem do środka.
     *
     * @return Kontener HBox.
     */
    private HBox createHBox() {
        HBox organizer = new HBox(12);
        organizer.setAlignment(Pos.CENTER);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        return organizer;
    }
    /**
     * Tworzy BorderPane z dostarczonym kontenerem HBox umieszczonym w centralnym miejscu.
     * Kolor tła BorderPane ustawiony jest na ciemnoszary.
     *
     * @param organizer Kontener HBox, który ma być dodany do BorderPane.
     * @return BorderPane zawierający dostarczony kontener HBox.
     */
    private BorderPane createManager(HBox organizer) {
        BorderPane root = new BorderPane(organizer);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    /**
     * Zarządza sceną dla głównego okna aplikacji, ustawiając dostarczony BorderPane jako root.
     * Zastosowuje także arkusz stylów dla sceny i pokazuje okno.
     *
     * @param primaryStage Główne okno aplikacji.
     * @param manager BorderPane zawierający układ UI.
     */
    private void manageScene(Stage primaryStage, BorderPane manager) {
        Scene scene = new Scene(manager, 1200, 900);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setTitle("Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    /**
     * Obsługuje logikę nasłuchiwania wiadomości od serwera, przetwarza je i aktualizuje stan gry.
     * Obsługuje różne polecenia, takie jak "CLOSING", "LOST", "DRAW", "ENEMYRESIGNED", "MOVE", "MESSAGE" i inne.
     * Słuchacz działa na osobnym wątku, aby zapewnić nieblokujące aktualizacje UI.
     */
    private void listeningLogic() {
        Runnable mainListener = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                String move = user.getUserInput().receiveMessage();
                if (move == null) {
                    Platform.runLater(this::disconnect);
                    return;
                }
                if (quiting) continue;
                String[] moveSplit = move.split(",");
                switch (moveSplit[0]) {
                    case "CLOSING":
                    case "SOCKETERROR":
                        Platform.runLater(Board.this::disconnect);
                        return;
                    case "LOST":
                        thisSessionL++;
                        Platform.runLater(Board.this::refreshScoreText);
                        finishedMatch = true;
                        statusText.setText("LOST!");
                        break;
                    case "DRAW":
                        thisSessionD++;
                        Platform.runLater(Board.this::refreshScoreText);
                        finishedMatch = true;
                        statusText.setText("You tied!");
                        break;
                    case "ENEMYRESIGNED":
                        thisSessionW++;
                        Platform.runLater(Board.this::refreshScoreText);
                        finishedMatch = true;
                        moved = true;
                        Platform.runLater(Board.this::quit);
                        break;
                    case "ENEMYQUIT":
                        finishedMatch = true;
                        moved = true;
                        Platform.runLater(Board.this::quit);
                        break;
                    case "REMATCH":
                        statusText.setText("The opponent wants a rematch!");
                        otherSideRematch = true;
                        break;
                    case "ACCEPT":
                        otherSideRematch = false;
                        finishedMatch = false;
                        Platform.runLater(() -> {
                            symbolUsed[0] = moveSplit[1];
                            symbolUsed[1] = moveSplit[2];
                            resetBoard();
                        });
                        break;
                    case "MOVE":
                        String row = moveSplit[1];
                        String col = moveSplit[2];
                        int rowInt = Integer.parseInt(row);
                        int colInt = Integer.parseInt(col);
                        Platform.runLater(() -> {
                            board[rowInt][colInt].setText(symbolUsed[1]);
                            if (!checkWin()) checkDraw();
                        });
                        moved = false;
                        statusText.setText("Your turn!");
                        break;
                    case "MESSAGE":
                        Platform.runLater(() -> addMessage(true, moveSplit[1]));
                        break;
                    default:
                        break;
                }
            }
        };
        messageListener = new Thread(mainListener);
        messageListener.setDaemon(true);
        messageListener.start();
    }
    /**
     * Rezygnuje z gry i wysyła wiadomość rezygnacji do serwera.
     * Jeśli gra jeszcze trwa, wysyłany jest komunikat "RESIGNED", w przeciwnym razie "QUIT".
     */
    private void resign() {
        if (!quiting) {
            messageListener.interrupt();
            try {
                messageListener.join();
            } catch (InterruptedException ignored) {}
            if (!finishedMatch) {
                thisSessionL++;
                refreshScoreText();
                user.getUserOutput().sendMessage("RESIGNED");
            } else {
                user.getUserOutput().sendMessage("QUIT");
            }
            onResign.run();
            quiting = true;
        }
    }
    /**
     * Kończy grę poprzez wyjście i wysłanie komunikatu o zakończeniu gry do serwera.
     * Wiadomość statusowa informuje użytkownika, że przeciwnik zrezygnował i gra została zakończona.
     */
    private void quit() {
        messageListener.interrupt();
        try {
            messageListener.join();
        } catch (InterruptedException ignored) {}
        quiting = true;
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(_ -> onResign.run());
        visiblePause.play();
        statusText.setText("The opponent has resigned! I am ending the game.");
        finishedMatch = true;
        moved = true;
    }
    /**
     * Rozłącza się od serwera, kończy grę i kończy działanie aplikacji.
     */
    private void disconnect() {
        messageListener.interrupt();
        try {
            messageListener.join();
        } catch (InterruptedException ignored) {}
        user.closeConnection();
        System.exit(-2);
    }
    /**
     * Wysyła żądanie remisu do serwera lub akceptuje ofertę remisu, jeśli gra się zakończyła.
     * Jeśli przeciwnik chce remisu, aktualizowany jest status wiadomości odpowiednio.
     */
    private void rematch() {
        if (finishedMatch && !quiting) {
            if (!otherSideRematch) {
                user.getUserOutput().sendMessage("REMATCH");
                statusText.setText("You want a rematch!");
            } else {
                user.getUserOutput().sendMessage("ACCEPT");
                Platform.runLater(this::resetBoard);
                finishedMatch = false;
            }
        }
    }
    /**
     * Ustawia akcję, która ma być wykonana po rezygnacji użytkownika z gry.
     *
     * @param onResign Runnable określający akcję po rezygnacji.
     */
    public void setOnResign(Runnable onResign) {
        this.onResign = onResign;
    }

}

