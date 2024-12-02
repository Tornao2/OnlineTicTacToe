package com.example.javaonlineproject;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import static javafx.scene.paint.Color.WHITE;

public class Board {
    private Thread messageListener;
    private Runnable onResign;
    private Runnable onDisconnect;
    private Text statusText = new Text();
    private Text scoreText = new Text();
    private final Button[][] board = new Button[3][3];
    private String[] symbolUsed;
    private Boolean moved;
    private Boolean quiting = false;
    private boolean otherSideRematch = false;
    private boolean finishedMatch = false;
    private int thisSessionW = 0;
    private int thisSessionD = 0;
    private int thisSessionL = 0;
    private UserInfo user;
    private String enemyName;
    private final VBox chatView = new VBox();
    TextField chatField = new TextField();
    ScrollPane scrollPane;

    private void createScoreText() {
        user.getUserOutput().sendMessage("NAME");
        enemyName = user.getUserInput().receiveMessage();
        scoreText = new Text("You 0-0-0 " + enemyName);
        scoreText.setFill(WHITE);
        scoreText.setFont(new Font(26));
    }
    private void refreshScoreText() {
        scoreText.setText("You " + thisSessionW + "-" + thisSessionD + "-" + thisSessionL + " " + enemyName);
    }
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
    private GridPane initializeBoard() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_CENTER);
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        gridPane.getStyleClass().add("grid-pane"); // Dodanie klasy CSS
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Button cell = new Button("");
                cell.getStyleClass().add("button"); // Dodanie klasy CSS
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
    private void initChatView() {
        chatField.setPrefWidth(450);
        scrollPane = new ScrollPane(chatView);
        scrollPane.setStyle("-fx-background-color: white;");
        scrollPane.setPrefSize(600, 600);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        user.getUserOutput().sendMessage("GETCHATHISTORY");
        String[] listOfMessages = user.getUserInput().receiveMessage().split(",");
        for(int i = 0; i < listOfMessages.length; i += 2){
            Label messageLabel = new Label(listOfMessages[i+1].replace("~", ","));
            messageLabel.setFont(new Font(14));
            messageLabel.setMaxWidth(400);
            messageLabel.setWrapText(true);
            messageLabel.setPadding(new Insets(3, 3, 0, 3));
            HBox messageContainer = new HBox(messageLabel);
            if (listOfMessages[i].equals("ENEMY"))
                messageContainer.setAlignment(Pos.CENTER_LEFT);
            else
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
            chatView.getChildren().add(messageContainer);
            scrollPane.setVvalue(1.0);
        }
    }
    private Button createResignButton() {
        Button resign = new Button("Resign");
        resign.setFont(new Font(16));
        resign.setOnAction(_ -> resign());
        return resign;
    }
    private Button createRematchButton() {
        Button rematch = new Button("Rematch");
        rematch.setFont(new Font(16));
        rematch.setOnAction(_ -> rematch());
        return rematch;
    }
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setAlignment(Pos.CENTER);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        return organizer;
    }
    private HBox createHBox() {
        HBox organizer = new HBox(12);
        organizer.setAlignment(Pos.CENTER);
        organizer.setPadding(new Insets(8, 8, 10, 8));
        return organizer;
    }
    private Button sendMessageButton() {
        Button send = new Button("Send");
        send.setFont(new Font(16));
        send.setOnAction(_ -> addMessage(false, chatField.getText()));
        return send;
    }
    private void initSendingField() {
        chatField.setFont(new Font(16));
    }
    private BorderPane createManager(HBox organizer){
        BorderPane root = new BorderPane(organizer);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    private void manageScene(Stage primaryStage, BorderPane manager) {
        Scene scene = new Scene(manager, 1200, 900);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm()); // Dodanie pliku CSS
        primaryStage.setTitle("Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void setTurns() {
        moved = !symbolUsed[0].equals("X");
        if (moved)
            statusText.setText("Enemy's turn!");
        else
            statusText.setText("Your turn!");

    }
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
        Button rematchButton= createRematchButton();
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
    private void listeningLogic() {
        Runnable mainListener = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                String move = user.getUserInput().receiveMessage();
                if (move == null || quiting) continue;
                String[] moveSplit = move.split(",");
                switch (moveSplit[0]) {
                    case "SOCKETERROR":
                        Platform.runLater(Board.this::disconnect);
                        return;
                    case "LOST":
                        thisSessionL++;
                        Platform.runLater(Board.this::refreshScoreText);
                        finishedMatch = true;
                        statusText.setText("You lost!");
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
                        statusText.setText("Enemy wants a rematch!");
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
                        Platform.runLater(() -> {
                            addMessage(true, moveSplit[1]);
                        });
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
    private void resetBoard() {
        for (Button[] row : board) {
            for (Button cell : row) {
                cell.setText(""); // Wyczyszczenie tekstu w przyciskach
                cell.setStyle(null); // Usunięcie wszelkich stylów inline
                cell.getStyleClass().add("button"); // Przywrócenie klasy CSS
            }
        }

        finishedMatch = false; // Oznaczenie, że nowa gra jest rozpoczęta
        otherSideRematch = false; // Reset flagi rematchu
        setTurns(); // Przywrócenie kolejności graczy na podstawie symbolu
    }


    private void rematch() {
        if (finishedMatch && !quiting) {
            if (!otherSideRematch) {
                user.getUserOutput().sendMessage("REMATCH");
                statusText.setText("You want a rematch!");
            } else {
                user.getUserOutput().sendMessage("ACCEPT");
                Platform.runLater(this::resetBoard); // Reset planszy po zaakceptowaniu rematchu
                finishedMatch = false; // Oznaczenie, że gra nie jest zakończona
            }
        }
    }


    private void resign() {
        if (!quiting) {
            messageListener.interrupt();
            try {
                messageListener.join();
            } catch (InterruptedException _) {}
            if (!finishedMatch) {
                thisSessionL++;
                refreshScoreText();
                user.getUserOutput().sendMessage("RESIGNED");
            } else user.getUserOutput().sendMessage("QUIT");
            onResign.run();
            quiting = true;
        }
    }
    private void quit() {
        messageListener.interrupt();
        try {
            messageListener.join();
        } catch (InterruptedException _) {}
        quiting = true;
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(_ -> onResign.run());
        visiblePause.play();
        statusText.setText("Enemy has resigned! Quiting the match");
        finishedMatch = true;
        moved = true;
    }
    private void disconnect() {
        messageListener.interrupt();
        try {
            messageListener.join();
        } catch (InterruptedException _) {}
        user.closeConnection();
        onDisconnect.run();
    }
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
                messageContainer.setAlignment(Pos.CENTER_RIGHT);
                user.getUserOutput().sendMessage("MESSAGE," + message);
                chatField.clear();
            } else
                messageContainer.setAlignment(Pos.CENTER_LEFT);
            chatView.getChildren().add(messageContainer);
            scrollPane.setVvalue(1.0);
        }
    }
    public void setOnResign(Runnable onResign){
        this.onResign = onResign;
    }
    public void setOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect = onDisconnect;
    }
}
