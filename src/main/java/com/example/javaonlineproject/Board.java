package com.example.javaonlineproject;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Board {
    private Button[][] board;
    private char player;
    private GridPane grid;
    private Network network;
    private AnchorPane root;

    public Board(Network network) {
        this.network = network;
        this.board = new Button[3][3];
        this.player = 'X';
        this.grid = new GridPane();
        initializeBoard();
            }

    public void start(Stage gameStage){
    }

    private void initializeBoard() {
        root = new AnchorPane();
        root.setPrefSize(800, 600);
        root.setStyle("-fx-background-color: #000000");
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Button cell = new Button("");
                cell.setMinSize(100, 100);
                final int r = row;
                final int c = column;
                cell.setOnAction(event -> handleMove(r, c, cell));
                board[r][c] = cell;
                grid.add(cell, column, row);
            }
        }
    }

    private void handleMove(int row, int column, Button cell) {
        if (cell.getText().isEmpty()) {
            cell.setText(String.valueOf(player));
            network.sendMessage(row + "," + column); // Send move over the network
            if (checkWin()) {
                System.out.println(player + " wins!");
                network.sendMessage("WIN");
            } else if (checkDraw()) {
                System.out.println("Draw");
                network.sendMessage("DRAW");
            } else {
                player = (player == 'X' ? 'O' : 'X'); // Switch player
                receiveMove(); // Get opponent's move
            }
        }
    }

    private void receiveMove() {
        String move = network.receiveMessage();
        if (move != null) {
            if (move.equals("WIN")) {
                System.out.println("Opponent won!");
            } else if (move.equals("DRAW")) {
                System.out.println("It's a draw!");
            } else {
                String[] parts = move.split(",");
                int row = Integer.parseInt(parts[0]);
                int column = Integer.parseInt(parts[1]);
                if (board[row][column].getText().isEmpty()) {
                    board[row][column].setText(String.valueOf(player));
                    if (checkWin()) {
                        System.out.println(player + " wins!");
                    } else if (checkDraw()) {
                        System.out.println("Draw");
                    } else {
                        player = (player == 'X' ? 'O' : 'X');
                    }
                }
            }
        }
    }

    private Boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].getText().equals(board[i][1].getText()) &&
                    board[i][0].getText().equals(board[i][2].getText()) &&
                    !board[i][0].getText().isEmpty()) {
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[0][i].getText().equals(board[1][i].getText()) &&
                    board[0][i].getText().equals(board[2][i].getText()) &&
                    !board[0][i].getText().isEmpty()) {
                return true;
            }
        }
        if (board[0][0].getText().equals(board[1][1].getText()) &&
                board[0][0].getText().equals(board[2][2].getText()) &&
                !board[0][0].getText().isEmpty()) {
            return true;
        }
        if (board[0][2].getText().equals(board[1][1].getText()) &&
                board[0][2].getText().equals(board[2][0].getText()) &&
                !board[0][2].getText().isEmpty()) {
            return true;
        }
        return false;
    }

    private Boolean checkDraw() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return !checkWin();
    }

    public GridPane getGridPane() {
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }
}
