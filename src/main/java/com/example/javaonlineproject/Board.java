package com.example.javaonlineproject;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Board {
    private final Button[][] board;
    private String symbolUsed;
    private Boolean moved;
    private int player1Wins = 0, draws = 0, player2Wins = 0;
    private Connection connection;
    Text player1ScoreText;
    Text player2ScoreText;
    Thread messageListenerThread = null;

    public Board() {
        this.board = new Button[3][3];
    }
    private void initializePlayer1ScoreText() {
        player1ScoreText = new Text();
        player1ScoreText.setStyle("-fx-fill: white;");
        player1ScoreText.setFont(new Font(16));
    }
    private void initializePlayer2ScoreText(){
        player2ScoreText = new Text();
        player2ScoreText.setStyle("-fx-fill: white;");
        player2ScoreText.setFont(new Font(16));
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
    private VBox createVBox() {
        VBox organizer = new VBox(12);
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

    public void start(Stage primaryStage, Connection connection) {
        this.connection = connection;
        if (connection.getIsServer()) {
            symbolUsed = "X";
            moved = false;
        } else {
            symbolUsed = "O";
            moved = true;
        }
        initializePlayer1ScoreText();
        initializePlayer2ScoreText();
        GridPane gameGrid = initializeBoard();
        Button exitButton = createExitButton();
        VBox organizer = createVBox();
        organizer.getChildren().addAll(player1ScoreText, player2ScoreText, gameGrid, exitButton);
        BorderPane manager = createManager(organizer);
        manageScene(primaryStage, manager);
        Thread messageListenerThread = new Thread(new MessageListener());
        messageListenerThread.start();
    }

    private void handleMove(int row, int column, Button cell) {
        if (!moved) {
            if (cell.getText().isEmpty()) {
                cell.setText(symbolUsed);
                if (checkWin()) {
                    System.out.println("You win!"); //Debugging
                    if (connection.getIsServer()) {
                        player1Wins++;
                    } else {
                        player2Wins++;
                    }
                    connection.sendMessage("WIN");
                    updateScores();
                    resetBoard();
                } else if (checkDraw()) {
                    System.out.println("Draw"); //Debugging
                    draws++;
                    connection.sendMessage("DRAW");
                    updateScores();
                    resetBoard();
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
                switch (move) {
                    case null:
                        break;
                    case "WIN":
                        System.out.println("Opponent won!"); // Debugging
                        if (connection.getIsServer()) {
                            player2Wins++;
                        } else {
                            player1Wins++;
                        }
                        Platform.runLater(() -> {
                            updateScores();
                            resetBoard();
                        });
                        break;
                    case "DRAW":
                        System.out.println("It's a draw!"); // Debugging
                        draws++;
                        Platform.runLater(() -> {
                            updateScores();
                            resetBoard();
                        });
                        break;
                    case "CLOSING":
                        exit();
                        break;
                    default:
                        String[] parts = move.split(",");
                        int row = Integer.parseInt(parts[0]);
                        int column = Integer.parseInt(parts[1]);
                        Platform.runLater(() -> {
                            if (connection.getIsServer()) {
                                board[row][column].setText("O");
                            } else {
                                board[row][column].setText("X");
                            }
                        });
                        moved = false;
                }
            }
        }
    }
    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].getText().equals(symbolUsed) &&
                    board[i][1].getText().equals(symbolUsed) &&
                    board[i][2].getText().equals(symbolUsed)) {
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[0][i].getText().equals(symbolUsed) &&
                    board[1][i].getText().equals(symbolUsed) &&
                    board[2][i].getText().equals(symbolUsed)) {
                return true;
            }
        }
        return board[0][0].getText().equals(symbolUsed) &&
                board[1][1].getText().equals(symbolUsed) &&
                board[2][2].getText().equals(symbolUsed) ||
                board[0][2].getText().equals(symbolUsed) &&
                        board[1][1].getText().equals(symbolUsed) &&
                        board[2][0].getText().equals(symbolUsed);
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
        player1ScoreText.setText("Player 1 (X) Score: " + player1Wins + "-" + draws + "-" + player2Wins);
        player2ScoreText.setText("Player 2 (O) Score: " + player2Wins + "-" + draws + "-" + player1Wins);
    }
    private void resetBoard() {
        for (Button[] row : board) {
            for (Button cell : row) {
                cell.setText("");
            }
        }
        moved = !connection.getIsServer();
    }
    private void exit() {
        if (messageListenerThread != null) {
            messageListenerThread.interrupt();
            try {
                messageListenerThread.join();
            } catch (InterruptedException e) {
                System.out.println("Error while waiting for thread to finish: " + e.getMessage());
            }
        }
        connection.closeConnection();
        System.exit(0);
    }
}
