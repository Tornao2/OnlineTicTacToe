package com.example.javaonlineproject;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ServerLogic {
    Thread connectingThread;
    ArrayList <Thread> listenerThreads = new ArrayList<>();
    ServerSocket serverSocket;
    LinkedHashMap<String, UserInfo> userMap = new LinkedHashMap <>();
    ArrayList <UserInfo> waitingToPlay = new ArrayList<>();

    private Button createExitButton() {
        Button loginButton = new Button("Exit");
        loginButton.setFont(new Font(20));
        loginButton.setOnAction(_ -> stopAll());
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
        Runnable mainListener = () -> {
            UserInfo userServed = userMap.lastEntry().getValue();
            while (!Thread.currentThread().isInterrupted()) {
                String move = userServed.getUserInput().receiveMessage();
                if (move == null) {
                    continue;
                }
                switch (move){
                    case "GETENEMY":
                        String enemyList = makeEnemyList(userServed);
                        waitingToPlay.add(userServed);
                        userServed.getUserOutput().sendMessage(enemyList);
                        sendListToEveryoneBesides(userServed);
                        break;
                    case "REMOVE":
                        waitingToPlay.remove(userServed);
                        sendListToEveryoneBesides(userServed);
                        break;
                    case "SOCKETERROR":
                        stopThisUser(userServed);
                        return;
                    default:
                        break;
                }
            }
        };
        Runnable connectionListener = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                UserInfo temp = new UserInfo();
                Socket connection;
                try {
                    connection = serverSocket.accept();
                } catch (IOException _) {
                    return;
                }
                temp.setUsersocket(connection);
                temp.setUserinput(connection);
                temp.setUseroutput(connection);
                String loginAttempt;
                loginAttempt = temp.getUserInput().receiveMessage();
                String[]data = loginAttempt.split(",");
                //Tutaj można dodać sprawdzanie hasła i loginu z data[1] i data[2]
                temp.setUsername(data[1]);
                temp.getUserOutput().sendMessage("ALLOWED");
                userMap.put(temp.getUsername(), temp);
                Thread listener = new Thread(mainListener);
                listener.setDaemon(true);
                listenerThreads.add(listener);
                listener.start();
            }
        };
        connectingThread = new Thread(connectionListener);
        connectingThread.setDaemon(true);
        connectingThread.start();
    }

    private void sendListToEveryoneBesides(UserInfo userServed) {
        for (UserInfo users: waitingToPlay) {
            String enemyList = makeEnemyList(users);
            if (!users.getUsername().equals(userServed.getUsername())) {
                users.getUserOutput().sendMessage(enemyList);
            }
        }
    }
    private String makeEnemyList(UserInfo userServed) {
        String temp = "ENEMIES";
        for (UserInfo users: waitingToPlay) {
            if (!users.getUsername().equals(userServed.getUsername())) {
                temp = temp.concat("," + users.getUsername());
            }
        }
        return temp;
    }
    private void stopAll() {
        connectingThread.interrupt();
        for (Thread thread: listenerThreads)
            thread.interrupt();
        for (UserInfo reader : userMap.values()) {
            reader.closeConnection();
        }
        userMap.clear();
        listenerThreads.clear();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
    private void stopThisUser(UserInfo userServed) {
        userServed.closeConnection();
        userMap.remove(userServed.getUsername());
    }
}
