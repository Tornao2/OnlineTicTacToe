package com.example.javaonlineproject;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class Board {
    private Button[][] board;
    private char player;
    private GridPane grid;

    public Board() {
        board = new Button[3][3];
        player = 'X';
        grid = new GridPane(); // Dzieli na siatke
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Button cell = new Button("");
                cell.setMinSize(100, 100);
                cell.setTranslateX(300);
                cell.setTranslateY(150);
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
            if (checkWin()) {
                System.out.println(player + " Wygrywa!");
            } else if (checkDraw()) {
                System.out.println("Remis");
            } else {
                player = (player == 'X' ? 'O' : 'X');
            }
        }
    }

    private Boolean checkWin() {
        /*
        int count = 0;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                //Zacząłem robić ale idk czy będziemy to mieć eventually jutro skoncze
            }
        }*/
        return false;
    }

    private Boolean checkDraw() {
        // To tez
        return false;
    }

    public GridPane getGridPane() {
        return grid;
    }
}
