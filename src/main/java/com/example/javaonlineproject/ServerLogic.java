package com.example.javaonlineproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerLogic extends Application {
    private Thread connectingThread;
    private final ArrayList <Thread> listenerThreads = new ArrayList<>();
    private ServerSocket serverSocket;
    private final LinkedHashMap<String, UserInfo> userMap = new LinkedHashMap <>();
    private final ArrayList <UserInfo> waitingToPlay = new ArrayList<>();
    private final HashMap<UserInfo, UserInfo> playersInProgress = new HashMap<>();
    private static final String FILEPATH = "LoginData.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

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
                if (move == null) continue;
                String[] moveSplit = move.split(",");
                switch (moveSplit[0]){
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
                        for(UserInfo user: waitingToPlay)
                            if (user == userServed) {
                                waitingToPlay.remove(userServed);
                                sendListToEveryoneBesides(userServed);
                                break;
                            }
                        if (playersInProgress.containsKey(userServed)) {
                            playersInProgress.get(userServed).getUserOutput().sendMessage("ENEMYRESIGNED");
                            playersInProgress.remove(playersInProgress.get(userServed));
                            playersInProgress.remove(userServed);
                        }
                        stopThisUser(userServed);
                        return;
                    case "INVITE":
                        String enemyNick = moveSplit[1];
                        UserInfo searchedUser = userMap.get(enemyNick);
                        searchedUser.getUserOutput().sendMessage("INVITED," + userServed.getUsername());
                        break;
                    case "PLAY":
                        String firstNick = moveSplit[1];
                        String secondNick = userServed.getUsername();
                        UserInfo firstUser = userMap.get(firstNick);
                        firstUser.getUserOutput().sendMessage("MATCH,X,O");
                        UserInfo secondUser = userMap.get(secondNick);
                        secondUser.getUserOutput().sendMessage("MATCH,O,X");
                        waitingToPlay.remove(firstUser);
                        waitingToPlay.remove(secondUser);
                        sendListToEveryoneBesides(null);
                        playersInProgress.put(firstUser, secondUser);
                        playersInProgress.put(secondUser, firstUser);
                        break;
                    case "WIN":
                        //+1 lose dla tego co przegrał + 1 win dla drugiego zapisać do pliku
                        playersInProgress.get(userServed).getUserOutput().sendMessage("LOST");
                        break;
                    case "DRAW":
                        //+1 draw dla obu zapisać w pliku
                        playersInProgress.get(userServed).getUserOutput().sendMessage("DRAW");
                        break;
                    case "MOVE":
                        String row = moveSplit[1];
                        String col = moveSplit[2];
                        playersInProgress.get(userServed).getUserOutput().sendMessage("MOVE," + row + "," + col);
                        break;
                    case "RESIGNED":
                        //+1 lose dla tego co zrezygnował + 1 win dla drugiego zapisać do pliku
                        playersInProgress.get(userServed).getUserOutput().sendMessage("ENEMYRESIGNED");
                        playersInProgress.remove(playersInProgress.get(userServed));
                        playersInProgress.remove(userServed);
                        break;
                    case "QUIT":
                        playersInProgress.get(userServed).getUserOutput().sendMessage("ENEMYQUIT");
                        playersInProgress.remove(playersInProgress.get(userServed));
                        playersInProgress.remove(userServed);
                        break;
                    case "REMATCH":
                        playersInProgress.get(userServed).getUserOutput().sendMessage("REMATCH");
                        break;
                    case "ACCEPT":
                        playersInProgress.get(userServed).getUserOutput().sendMessage("ACCEPT,O,X");
                        userServed.getUserOutput().sendMessage("ACCEPT,X,O");
                        break;
                    case "NAME":
                        userServed.getUserOutput().sendMessage(playersInProgress.get((userServed)).getUsername());
                        break;
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
                temp.setUserSocket(connection);
                temp.setUserInput(connection);
                temp.setUserOutput(connection);
                String loginAttempt;
                loginAttempt = temp.getUserInput().receiveMessage();
                String[]data = loginAttempt.split(",");
                //Tutaj można dodać sprawdzanie hasła i loginu z data[1] i data[2]
                File file = new File(FILEPATH);
                System.out.println("File path: " + file.getAbsolutePath());
                if (!file.exists()) {System.out.println("File dont exist");} //Debuging

                //Dodawanie nowego gracza
                if("SIGNUP".equals(data[0])){
                    if(isUsernameCorrect(data[1])){
                        temp.getUserOutput().sendMessage("USERNAME_TAKEN");
                        continue;
                    }
                    registerNewUser(data[1], data[2]);
                    temp.setUsername(data[1]);
                    temp.getUserOutput().sendMessage("ALLOWED");
                    userMap.put(data[1], temp);
                    Thread listener = new Thread(mainListener);
                    listener.setDaemon(true);
                    listenerThreads.add(listener);
                    listener.start();
                }

                if (userMap.containsKey(data[1])) {
                    temp.getUserOutput().sendMessage("ALREADY_LOGGED_IN");
                    continue;
                }

                if (isUsernameCorrect(data[1])) {
                    if(checkPassword(data[1], data[2])) {
                        temp.setUsername(data[1]);
                        temp.getUserOutput().sendMessage("ALLOWED");
                        userMap.put(temp.getUsername(), temp);
                        Thread listener = new Thread(mainListener);
                        listener.setDaemon(true);
                        listenerThreads.add(listener);
                        listener.start();
                    }
                    else{
                        temp.getUserOutput().sendMessage("Wrong password!");
                    }
                }
                else {
                    registerNewUser(data[1], data[2]);
                    temp.setUsername(data[1]);
                    temp.getUserOutput().sendMessage("ALLOWED");
                    userMap.put(temp.getUsername(), temp);
                    Thread listener = new Thread(mainListener);
                    listener.setDaemon(true);
                    listenerThreads.add(listener);
                    listener.start();
                }
            }
        };
        connectingThread = new Thread(connectionListener);
        connectingThread.setDaemon(true);
        connectingThread.start();
    }

    private boolean isUsernameCorrect(String username){
        List<LoginData> users = loadUsersFromFile();
        if (users != null)
            for (LoginData user : users)
                if (user.getLogin().equals(username))
                    return true;
        return false;
    }
    private boolean checkPassword(String username, String password){
        List<LoginData> users = loadUsersFromFile();
        if (users != null)
            for (LoginData user: users)
                if (user.getLogin().equals(username) && user.getPassword().equals(password))
                    return true;
        return false;
    }
    private List<LoginData> loadUsersFromFile() {
        File file = new File(FILEPATH);
        if(!file.exists()) return new ArrayList<>();
        try {
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, LoginData.class));
        } catch (IOException _) {
            System.err.println("loadUsersFromFile");
            return null;
        }
    }
    private void registerNewUser(String username, String password) {
        List<LoginData> users = loadUsersFromFile();
        if (users != null) {
            users.add(new LoginData(username, password));
            saveUsersToFile(users);
        }
    }
    private void saveUsersToFile(List<LoginData> users){
        try {
            objectMapper.writeValue(new File(FILEPATH), users);
        } catch (IOException _) {
            System.err.println("saveUsersToFile");
        }
    }

    private void sendListToEveryoneBesides(UserInfo userServed) {
        for (UserInfo users: waitingToPlay) {
            String enemyList = makeEnemyList(users);
            if (!users.getUsername().equals(userServed.getUsername()))  users.getUserOutput().sendMessage("REFRESH"+ enemyList);
        }
    }
    private String makeEnemyList(UserInfo userServed) {
        String temp = "";
        for (UserInfo users: waitingToPlay)
            if (!users.getUsername().equals(userServed.getUsername()))
                temp = temp.concat("," + users.getUsername());
        return temp;
    }
    private void stopAll() {
        connectingThread.interrupt();
        for (Thread thread: listenerThreads) thread.interrupt();
        for (UserInfo reader : userMap.values()) reader.closeConnection();
        userMap.clear();
        listenerThreads.clear();
        if (serverSocket != null && !serverSocket.isClosed())
            try {
                serverSocket.close();
            } catch (IOException _) {
                System.err.println("stopAll exception");
            }
        System.exit(0);
    }
    private void stopThisUser(UserInfo userServed) {
        userServed.closeConnection();
        userMap.remove(userServed.getUsername());
    }
}
