package com.example.javaonlineproject;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;

public class Board {
    private Button[][] board;
    private char player;
    private GridPane grid;
    private Network network;
    private AnchorPane root;
    private int win = 0, draw = 0, lose = 0;
    private Text text = new Text();
    private Text text2 = new Text();

    public Board(Network network) {
        this.network = network;
        this.board = new Button[3][3];
        this.player = 'X';
        this.grid = new GridPane();
        initializeBoard();
        initializeScoreTexts();
            }

    public void start(Stage gameStage){}

    private void initializeScoreTexts() {
        text.setX(100);
        text.setY(200);
        text.setStyle("-fx-fill: white;");
        text.setFont(Font.font(20));

        text2.setX(700);
        text2.setY(200);
        text2.setStyle("-fx-fill: white;");
        text2.setFont(Font.font(20));

        root.getChildren().addAll(text, text2);
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
            cell.setText(String.valueOf(player));
            network.sendMessage(row + "," + column); // Send move over the network
            textScores();
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
                        win += 1;
                    } else if (checkDraw()) {
                        System.out.println("Draw");
                        draw += 1;
                    } else {
                        player = (player == 'X' ? 'O' : 'X');
                        lose += 1;
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

    public void textScores() {
  /*      if (checkWin()) {
            win += 1;
        } else if (checkDraw()) {
            draw += 1;
        } else {
            lose += 1;
        }
*/
        text.setText(player + " score\n" + win + "-" + draw + "-" + lose);
        text2.setText("Enemy score\n" + lose + "-" + draw + "-" + win);
    }

    public AnchorPane getRootPane() {
        return root;
    }
}
/*
Do poprawy jest logika checkwin i checkdraw  najprawdobodobniej dodanie zmiennej player2  da rade, poza tym  poprawa wypisywania wyniku
twojego i gracza
Na 1 kamien jeszcze do zrobienia jeszcze mozliwosc powtórzenia meczu
Poza tym wybieranie pól też i będzie git
 */