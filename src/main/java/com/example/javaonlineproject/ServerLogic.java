package com.example.javaonlineproject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerLogic {
    Thread connectingThread;
    ServerSocket serverSocket;
    HashMap<String, BufferedReader> userReaders = new HashMap<>();
    HashMap<String, PrintWriter> userWriters = new HashMap<>();
    HashMap<String, Socket> userSockets = new HashMap<>();

    private Button createExitButton() {
        Button loginButton = new Button("Exit");
        loginButton.setFont(new Font(20));
        loginButton.setOnAction(_ -> stop());
        return loginButton;
    }
    private VBox createVBox() {
        VBox organizer = new VBox(12);
        organizer.setStyle("-fx-background-color: #1A1A1A;");
        organizer.setMinSize(300, 210);
        organizer.setPadding(new Insets(10, 8, 10, 8));
        organizer.setAlignment(Pos.CENTER);
        return organizer;
    }
    private void manageScene(VBox organizer, Stage primaryStage) {
        Scene scene = new Scene(organizer);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Server");
        primaryStage.show();
        organizer.requestFocus();
    }

    public void start(Stage primaryStage) {
        try {
            serverSocket = new ServerSocket(12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Button exitButton = createExitButton();
        VBox organizer = createVBox();
        organizer.getChildren().addAll(exitButton);
        manageScene(organizer, primaryStage);
        logic();
    }

    private void logic() {
        Runnable connectionListener = () -> {
            while (Thread.currentThread().isInterrupted()) {
                try {
                    Socket connection;
                    connection = serverSocket.accept();
                    BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    PrintWriter output = new PrintWriter(connection.getOutputStream(), true);
                    String loginAttempt;
                    while ((loginAttempt = receiveMessage(input) ) == null) {
                        Thread.sleep(200);
                    }
                    String[]data = loginAttempt.split(",");
                    userReaders.put(data[1], input);
                    userWriters.put(data[1], output);
                    userSockets.put(data[1], connection);
                    sendMessage("ALLOWED", output);
                } catch (InterruptedException e) {
                    return;
                }
                catch (IOException _) {

                }
            }
        };
        connectingThread = new Thread(connectionListener);
        connectingThread.start();
    }

    private void stop() {
        connectingThread.interrupt();
        for (BufferedReader reader : userReaders.values()) {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        userReaders.clear();
        for (PrintWriter writer : userWriters.values()) {
            if (writer != null) {
                writer.println("CLOSING");
                writer.close();
            }
        }
        userWriters.clear();
        for (Socket socket : userSockets.values()) {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        userSockets.clear();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public void sendMessage(String message, PrintWriter output) {
        output.println(message);
    }
    public String receiveMessage(BufferedReader input) {
        try {
            return input.readLine();
        } catch (IOException _) {
            return null;
        }
    }
}
