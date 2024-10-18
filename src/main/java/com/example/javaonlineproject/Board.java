    package com.example.javaonlineproject;

    import javafx.scene.control.Button;
    import javafx.scene.layout.GridPane;

    public class Board {
        private Button[][] board;
        private char player;
        private GridPane grid;
        private Network network;

        public Board(Network network) {
            this.network = network;
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
                network.sendMessage(row + "," + column);
                if (checkWin()) {
                    System.out.println(player + " Wygrywa!");
                } else if (checkDraw()) {
                    System.out.println("Remis");
                } else {
                    player = (player == 'X' ? 'O' : 'X');
                    receiveMove();
                }
            }
        }

        private void receiveMove() {
            String move = network.receiveMessage();
            if(move != null) {
                String parts[] = move.split(",");
                int row = Integer.parseInt(parts[0]);
                int column = Integer.parseInt(parts[1]);

                if(board[row][column].getText().isEmpty()) {
                    board[row][column].setText(String.valueOf(player));
                    if(checkWin()) {
                        System.out.println(player + " Wygrywa!");
                    }
                    else if (checkDraw()) {
                        System.out.println("Remis");
                    }
                    else{
                        player = (player == 'X' ? 'O' : 'X');
                    }
                }
            }
        }

        private Boolean checkWin() {
            //Sprawdzanie wierszy
            for (int i = 0; i < 3; i++) {
                if (board[i][0].getText().equals(board[i][1].getText()) &&
                        board[i][0].getText().equals(board[i][2].getText()) &&
                        !board[i][0].getText().isEmpty()) {
                    return true;
                }
            }
            // Sprawdzanie kolumn
            for (int i = 0; i < 3; i++) {
                if (board[0][i].getText().equals(board[1][i].getText()) &&
                        board[0][i].getText().equals(board[2][i].getText()) &&
                        !board[0][i].getText().isEmpty()) {
                    return true;
                }
            }
            // Sprawdzanie przekątnych
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
            return grid;
        }
    }
