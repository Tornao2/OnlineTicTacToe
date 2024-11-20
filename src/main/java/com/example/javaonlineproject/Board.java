package com.example.javaonlineproject;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Board {
    private Button[][] board;
    private char player1;
    private char player2;
    private char currentPlayer;
    private GridPane grid;
    private Network network;
    private AnchorPane root;
    private int player1Wins = 0, draws = 0, player2Wins = 0;
    private Text player1ScoreText = new Text();
    private Text player2ScoreText = new Text();

    public Board(Network network) {
        this.network = network;
        this.board = new Button[3][3];
        this.player1 = 'X';
        this.player2 = 'O';
        this.currentPlayer = player1;  // Start with player1
        this.grid = new GridPane();
        initializeBoard();
        initializeScoreTexts();
        startListeningForMoves(); // Uruchamiamy wątek nasłuchujący
    }

    private void initializeScoreTexts() {
        player1ScoreText.setX(100);
        player1ScoreText.setY(20);
        player1ScoreText.setStyle("-fx-fill: white;");
        player1ScoreText.setFont(Font.font(20));

        player2ScoreText.setX(400);
        player2ScoreText.setY(20);
        player2ScoreText.setStyle("-fx-fill: white;");
        player2ScoreText.setFont(Font.font(20));

        root.getChildren().addAll(player1ScoreText, player2ScoreText);
    }

    private void initializeBoard() {
        root = new AnchorPane();
        root.setPrefSize(800, 600);
        root.setStyle("-fx-background-color: #1A1A1A;");
        grid.setHgap(10);
        grid.setVgap(10);
        root.getChildren().add(grid);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Button cell = new Button("");
                cell.setMinSize(100, 100);
                cell.setStyle("-fx-background-color: #FFFFFF;");
                cell.prefHeight(300);
                cell.prefWidth(200);
                final int r = row;
                final int c = column;
                cell.setOnAction(event -> handleMove(r, c, cell));
                board[r][c] = cell;
                grid.add(cell, column, row);
            }
        }

        double gridWidth = 3 * 100 + 2 * 10;
        double gridHeight = 3 * 100 + 2 * 10;
        AnchorPane.setTopAnchor(grid, (root.getPrefHeight() - gridHeight) / 2);
        AnchorPane.setLeftAnchor(grid, (root.getPrefWidth() - gridWidth) / 2);
    }

    private void handleMove(int row, int column, Button cell) {
        if (cell.getText().isEmpty()) {
            cell.setText(String.valueOf(currentPlayer));
            network.sendMessage(row + "," + column); // Send move over the network

            if (checkWin(currentPlayer)) {
                System.out.println(currentPlayer + " wins!");
                if (currentPlayer == player1) {
                    player1Wins++;
                } else {
                    player2Wins++;
                }
                network.sendMessage("WIN");
                network.sendMessage("SCORES:" + player1Wins + ":" + draws + ":" + player2Wins); // Synchronizacja wyników
                updateScores();
                resetBoard();
            } else if (checkDraw()) {
                System.out.println("Draw");
                draws++;
                network.sendMessage("DRAW");
                network.sendMessage("SCORES:" + player1Wins + ":" + draws + ":" + player2Wins); // Synchronizacja wyników
                updateScores();
                resetBoard();
            } else {
                // Switch current player
                currentPlayer = (currentPlayer == player1) ? player2 : player1;
            }
        }
    }

    private void startListeningForMoves() {
        Thread listenerThread = new Thread(() -> {
            while (true) {
                String move = network.receiveMessage();
                if (move != null) {
                    Platform.runLater(() -> processMove(move)); // Przetwarzamy ruch w wątku JavaFX
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void processMove(String move) {
        if (move.startsWith("SCORES:")) {
            String[] parts = move.split(":");
            player1Wins = Integer.parseInt(parts[1]);
            draws = Integer.parseInt(parts[2]);
            player2Wins = Integer.parseInt(parts[3]);
            updateScores(); // Aktualizacja wyników na podstawie otrzymanych danych
        } else if (move.equals("WIN")) {
            System.out.println("Opponent won!");
            if (currentPlayer == player1) {
                player2Wins++;
            } else {
                player1Wins++;
            }
            updateScores();
            resetBoard();
        } else if (move.equals("DRAW")) {
            System.out.println("It's a draw!");
            draws++;
            updateScores();
            resetBoard();
        } else {
            String[] parts = move.split(",");
            int row = Integer.parseInt(parts[0]);
            int column = Integer.parseInt(parts[1]);
            if (board[row][column].getText().isEmpty()) {
                board[row][column].setText(String.valueOf(currentPlayer));
                if (checkWin(currentPlayer)) {
                    System.out.println(currentPlayer + " wins!");
                    if (currentPlayer == player1) {
                        player1Wins++;
                    } else {
                        player2Wins++;
                    }
                    updateScores();
                    resetBoard();
                } else if (checkDraw()) {
                    System.out.println("Draw");
                    draws++;
                    updateScores();
                    resetBoard();
                } else {
                    currentPlayer = (currentPlayer == player1) ? player2 : player1;
                }
            }
        }
    }

    private boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].getText().equals(String.valueOf(player)) &&
                    board[i][1].getText().equals(String.valueOf(player)) &&
                    board[i][2].getText().equals(String.valueOf(player))) {
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[0][i].getText().equals(String.valueOf(player)) &&
                    board[1][i].getText().equals(String.valueOf(player)) &&
                    board[2][i].getText().equals(String.valueOf(player))) {
                return true;
            }
        }
        return board[0][0].getText().equals(String.valueOf(player)) &&
                board[1][1].getText().equals(String.valueOf(player)) &&
                board[2][2].getText().equals(String.valueOf(player)) ||
                board[0][2].getText().equals(String.valueOf(player)) &&
                        board[1][1].getText().equals(String.valueOf(player)) &&
                        board[2][0].getText().equals(String.valueOf(player));
    }

    private boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return !checkWin(player1) && !checkWin(player2);
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
        currentPlayer = player1;  // Reset to player1 for new game
    }

    public AnchorPane getRootPane() {
        return root;
    }
}
