/*package com.example.javaonlineproject;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Board {
    private final Button[][] board;
    private String[] symbolUsed;
    private Boolean moved;
    private boolean otherSideRematch = false;
    private boolean finishedMatch = false;
    private int player1Wins = 0, draws = 0, player2Wins = 0;
    private Text scoreText;

    public Board() {
        this.board = new Button[3][3];
    }
    private void initializeScoreText() {
        scoreText = new Text();
        scoreText.setStyle("-fx-fill: white;");
        scoreText.setFont(new Font(24));
    }
    private GridPane initializeBoard() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_CENTER);
        gridPane.setHgap(6);
        gridPane.setVgap(6);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Button cell = new Button("");
                cell.setStyle("-fx-background-color: #FFFFFF;");
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
    private Button createExitButton() {
        Button exit = new Button("Exit");
        exit.setFont(new Font(16));
        exit.setOnAction(_ -> exit());
        return exit;
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
    private BorderPane createManager(VBox organizer){
        BorderPane root = new BorderPane(organizer);
        root.setStyle("-fx-background-color: #1A1A1A;");
        return root;
    }
    private void manageScene(Stage primaryStage, BorderPane manager) {
        Scene scene = new Scene(manager, 800, 600);
        primaryStage.setTitle("Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void start(Stage primaryStage, PlayerNetwork connection) {
        this.connection = connection;
        if (connection.getIsServer()) {
            symbolUsed = new String[]{"X", "O"};
            moved = false;
        } else {
            symbolUsed = new String[]{"O", "X"};
            moved = true;
        }
        initializeScoreText();
        GridPane gameGrid = initializeBoard();
        Button exitButton = createExitButton();
        Button rematchButton= createRematchButton();
        VBox organizer = createVBox();
        HBox buttons = createHBox();
        buttons.getChildren().addAll(rematchButton, exitButton);
        organizer.getChildren().addAll(scoreText, gameGrid, buttons);
        BorderPane manager = createManager(organizer);
        manageScene(primaryStage, manager);
        Thread messageListenerThread = new Thread(new MessageListener());
        messageListenerThread.setDaemon(true);
        messageListenerThread.start();
    }

    private void handleMove(int row, int column, Button cell) {
        if (!moved) {
            if (cell.getText().isEmpty()) {
                cell.setText(symbolUsed[0]);
                if (checkWin()) {
                    if (connection.getIsServer()) {
                        player1Wins++;
                    } else {
                        player2Wins++;
                    }
                    connection.sendMessage(row + "," + column);
                    connection.sendMessage("WIN");
                    updateScores();
                    finishedMatch = true;
                    moved = true;
                } else if (checkDraw()) {
                    draws++;
                    connection.sendMessage(row + "," + column);
                    connection.sendMessage("DRAW");
                    updateScores();
                    finishedMatch = true;
                    moved = true;
                } else {
                    connection.sendMessage(row + "," + column);
                    moved = true;
                }
            }
        }
    }
    class MessageListener implements Runnable {
        @Override
        public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    String move = connection.receiveMessage();
                    if (move == null) {
                        exit();
                        return;
                    }
                    switch (move) {
                        case "WIN":
                            if (connection.getIsServer()) {
                                player2Wins++;
                            } else {
                                player1Wins++;
                            }
                            Platform.runLater(Board.this::updateScores);
                            moved = true;
                            finishedMatch = true;
                            break;
                        case "DRAW":
                            draws++;
                            Platform.runLater(Board.this::updateScores);
                            moved = true;
                            finishedMatch = true;
                            break;
                        case "CLOSING":
                            exit();
                            break;
                        case "REMATCH":
                            otherSideRematch = true;
                            break;
                        case "ACCEPT":
                            otherSideRematch = false;
                            finishedMatch = false;
                            Platform.runLater(Board.this::resetBoard);
                            break;
                        default:
                            String[] parts = move.split(",");
                            int row = Integer.parseInt(parts[0]);
                            int column = Integer.parseInt(parts[1]);
                            Platform.runLater(() -> {
                                board[row][column].setText(symbolUsed[1]);
                                checkWin();
                            });
                            moved = false;
                    }
                }
        }
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
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return !checkWin();
    }
    private void updateScores() {
        scoreText.setVisible(true);
        scoreText.setText("Player 1 (X):  " + player1Wins + "-" + draws + "-" + player2Wins + "  Player 2 (O)");
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
        visiblePause.setOnFinished(_ -> scoreText.setVisible(false));
        visiblePause.play();
    }
    private void resetBoard() {
        for (Button[] row : board) {
            for (Button cell : row) {
                cell.setText("");
                cell.setStyle("-fx-background-color: #FFFFFF;");
            }
        }
        moved = !connection.getIsServer();
    }
    private void exit() {
        connection.closeConnection();
        System.exit(0);
    }
    private void rematch() {
        if (finishedMatch) {
            if (!otherSideRematch) {
                connection.sendMessage("REMATCH");
            } else {
                connection.sendMessage("ACCEPT");
                otherSideRematch = false;
                finishedMatch = false;
                resetBoard();
            }
        }
    }
}
*/