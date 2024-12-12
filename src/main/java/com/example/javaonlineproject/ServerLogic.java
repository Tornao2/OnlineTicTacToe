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
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

public class ServerLogic extends Application {
    private Thread connectingThread;
    private Thread broadCasting;
    private final ArrayList <Thread> listenerThreads = new ArrayList<>();
    private ServerSocket serverSocket;
    private final LinkedHashMap<String, UserInfo> userMap = new LinkedHashMap <>();
    private final ArrayList <UserInfo> waitingToPlay = new ArrayList<>();
    private final HashMap<UserInfo, UserInfo> playersInProgress = new HashMap<>();
    private static final String LOGINDATAFILEPATH = "LoginData.json";
    private static final String STATSFILEPATH = "StatsData.json";
    private static final String MATCHHISTORYFILEPATH = "MatchHistoryData.json";
    private static final String CHATHISTORYDATAFILEPATH = "ChatHistoryData.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Button createExitButton() {
        Button loginButton = new Button("Exit");
        loginButton.setFont(new Font(24.0));
        loginButton.getStyleClass().add("button");
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
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }
    public void start(Stage primaryStage) {
        try {
            serverSocket = new ServerSocket(12345);
        } catch (IOException e) {
            System.err.println("Serversocket" + e.getMessage());
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
                    case "GETMATCHHISTORY":
                        sendMatchHistoryToPlayer(userServed.getUsername());
                        break;
                    case "GETSTATS":
                        String playerUsername = userServed.getUsername();
                        sendStatsToPlayer(playerUsername);
                        break;
                    case "GETBESTPLAYERS":
                        sendBestPlayerToStats(userServed.getUsername());
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
                        updateStatsForUser(userServed.getUsername(), "WIN");
                        updateStatsForUser(playersInProgress.get(userServed).getUsername(), "LOSE");
                        playersInProgress.get(userServed).getUserOutput().sendMessage("LOST");
                        break;
                    case "DRAW":
                        updateStatsForUser(userServed.getUsername(), "DRAW");
                        updateStatsForUser(playersInProgress.get(userServed).getUsername(), "DRAW");
                        playersInProgress.get(userServed).getUserOutput().sendMessage("DRAW");
                        break;
                    case "MOVE":
                        String row = moveSplit[1];
                        String col = moveSplit[2];
                        playersInProgress.get(userServed).getUserOutput().sendMessage("MOVE," + row + "," + col);
                        break;
                    case "RESIGNED":
                        updateStatsForUser(userServed.getUsername(), "LOSE");
                        updateStatsForUser(playersInProgress.get(userServed).getUsername(), "WIN");
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
                    case "MESSAGE":
                        String message = moveSplit[1];
                        String receiverNick = playersInProgress.get(userServed).getUsername();
                        saveMessageToHistory(userServed.getUsername(), receiverNick, message);
                        break;
                    case "GETCHATHISTORY":
                       String opponentUsername = playersInProgress.get(userServed).getUsername();
                       sendChatHistoryToPlayer(userServed.getUsername(), opponentUsername);
                        // userServed.getUserOutput().sendMessage("ENEMY,ASDASDSA,PLAYER,ASDSADAS,ENEMY,DDSDSDS,PLAYER,FDFDF");
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
                if (userMap.containsKey(data[1])) {
                    temp.getUserOutput().sendMessage("ALREADYLOGGEDIN");
                    continue;
                }
                if(data[0].equals("SIGNUP")){
                    if(isUsernameCorrect(data[1])){
                        temp.getUserOutput().sendMessage("USERNAMETAKEN");
                        continue;
                    }
                    registerNewUser(data[1], data[2]);
                    handleLogin(data[1], temp, mainListener);
                } else if (isUsernameCorrect(data[1])) {
                    if(checkPassword(data[1], data[2]))
                        handleLogin(data[1],temp, mainListener);
                    else temp.getUserOutput().sendMessage("WRONGPASSWORD");
                } else
                    temp.getUserOutput().sendMessage("NOLOGIN");
            }
        };
        Runnable preConnection = () -> {
            MulticastSocket socket = null;
            try {
                socket = new MulticastSocket(12346);
            } catch (SocketException e) {
                System.err.println("Broadcasting socket error: " + e.getMessage());
                System.exit(-1);
            } catch (IOException e) {
                System.err.println("Broadcasting socket error: " + e.getMessage());
                System.exit(-11);
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                    InetAddress group = InetAddress.getByName("224.0.0.0");
                    while (interfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = interfaces.nextElement();
                        if (!networkInterface.supportsMulticast() || !networkInterface.isUp()) {
                            continue;
                        }
                        boolean hasIPv4 = networkInterface.getInterfaceAddresses().stream()
                                .anyMatch(addr -> addr.getAddress() instanceof Inet4Address);
                        if (!hasIPv4) {
                            continue;
                        }
                        try {
                            String dString = "REQUEST";
                            byte[] buf = dString.getBytes();
                            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 12346);
                            socket.setNetworkInterface(networkInterface);
                            socket.send(packet);
                        } catch (IOException _) {

                        }
                    }
                    Thread.sleep(500);
                } catch (UnknownHostException e) {
                    System.err.println("Group address error: " + e.getMessage());
                    System.exit(-3);
                } catch (IOException e) {
                    System.err.println("Socket error during iteration: " + e.getMessage());
                    System.exit(-4);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            socket.close();
        };
        broadCasting = new Thread(preConnection);
        broadCasting.setDaemon(true);
        broadCasting.start();
        connectingThread = new Thread(connectionListener);
        connectingThread.setDaemon(true);
        connectingThread.start();
    }

    private void sendChatHistoryToPlayer(String player1, String player2){
        List<ChatHistoryData> chatHistoryList = loadMessagesFromFile();
        List<String> filteredMessages = new ArrayList<>();
        for (ChatHistoryData chat : chatHistoryList) {
            if ((chat.getSender().equals(player1) && chat.getReciver().equals(player2)) ||
                    (chat.getSender().equals(player2) && chat.getReciver().equals(player1))) {
                filteredMessages.add(chat.getSender() + ": " + chat.getMessage());
            }
        }
        UserInfo user = userMap.get(player1);
        if(user != null){
            String chatHistory = String.join(",", filteredMessages);
            user.getUserOutput().sendMessage("CHATHISTORY:" + chatHistory);
        }
    }
    private void saveMessageToHistory(String senderNick, String receiverNick, String message){
        UserInfo sender = userMap.get(senderNick);
        UserInfo receiver = userMap.get(receiverNick);
        if(sender != null){
            ChatHistoryData chatHistory = new ChatHistoryData(senderNick, receiverNick, message);
            saveMessageToFile(chatHistory);
            receiver.getUserOutput().sendMessage("MESSAGE," + message);
        }
        else{
            System.out.println("Only 1 player online");
        }
    }

    private void saveMessageToFile(ChatHistoryData chatHistory) {
        List<ChatHistoryData> chatHistoryList = loadMessagesFromFile();
        System.out.println("Saving message: " + chatHistory.getMessage());
        chatHistoryList.add(chatHistory);
        File file = new File(CHATHISTORYDATAFILEPATH);
        try {
            objectMapper.writeValue(file, chatHistoryList);
        } catch (IOException e) {
            System.err.println("Failed to save chat history: " + e.getMessage());
        }
    }

    private List<ChatHistoryData> loadMessagesFromFile() {
        File file = new File(CHATHISTORYDATAFILEPATH);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();

        try {
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, ChatHistoryData.class));
        } catch (IOException e) {
            System.err.println("Failed to load chat history: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    private void updateStatsForUser(String username, String result) {
        List<StatsData> statsList = loadStatsFromFile();
        StatsData stats = getStatsForUser(username, statsList);
        if (stats == null) {
            stats = new StatsData(username, 0, 0, 0);
            statsList.add(stats);
        }
        switch (result) {
            case "WIN":
                stats.incrementWins();
                break;
            case "DRAW":
                stats.incrementDraws();
                break;
            case "LOSE":
                stats.incrementLosses();
                break;
        }
        try {
            String jsonResponse = objectMapper.writeValueAsString(statsList);
        } catch (IOException e) {
            System.err.println("Error converting stats to JSON: " + e.getMessage());
        }

        saveStatsToFile(statsList);
        UserInfo opponent = playersInProgress.get(userMap.get(username));
        saveMatchHistory(username, opponent.getUsername(), result);
    }
    private void saveMatchHistory(String playerUsername, String opponentUsername, String result){
        List<MatchHistoryData> historyList = loadMatchHistoryFromFile();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dateTime = sdf.format(new Date());
        MatchHistoryData matchHistory = new MatchHistoryData(dateTime, playerUsername, opponentUsername, result);
        historyList.add(matchHistory);
        saveMatchHistoryToFile(historyList);
    }
    private List<MatchHistoryData> loadMatchHistoryFromFile() {
        File file = new File(MATCHHISTORYFILEPATH);
        if (!file.exists() || file.length() == 0) return new ArrayList<>();
        try {
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, MatchHistoryData.class));
        } catch (IOException e) {
            System.err.println("Failed to load match history: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveMatchHistoryToFile(List<MatchHistoryData> historyList) {
        File file = new File(MATCHHISTORYFILEPATH);
        try {
            objectMapper.writeValue(file, historyList);
        } catch (IOException e) {
            System.err.println("Failed to save match history: " + e.getMessage());
        }
    }
        private void sendMatchHistoryToPlayer(String username) {
        List<MatchHistoryData> historyList = loadMatchHistoryFromFile();
        List<MatchHistoryData> playerHistory = new ArrayList<>();
        for (MatchHistoryData userMatch : historyList) {
            if (userMatch.getPlayer1username().equals(username)) {
                playerHistory.add(userMatch);
            }
        }
        String matchHistoryJson = convertMatchHistoryToJson(playerHistory);
        UserInfo user = userMap.get(username);
        user.getUserOutput().sendMessage("MATCHHISTORY: " + matchHistoryJson);
    }

    private String convertMatchHistoryToJson(List<MatchHistoryData> playerHistory) {
        try{
            return objectMapper.writeValueAsString(playerHistory);
        }catch(IOException e){
            System.err.println("Error converting match history to JSON: " + e.getMessage());
            return "ERROR";
        }
    }

    private StatsData getStatsForUser(String username, List<StatsData> statsList) {
        for (StatsData stats : statsList) {
            if (stats.getUsername().equals(username)) {
                return stats;
            }
        }
        return null;
    }

    private void saveStatsToFile(List<StatsData> statsList) {
        File file = new File(STATSFILEPATH);
        try {
            objectMapper.writeValue(file, statsList);
        } catch (IOException e) {
            System.err.println("Failed to save Stats: " + e.getMessage());
        }
    }

    private List<StatsData> loadStatsFromFile(){
        File file = new File(STATSFILEPATH);
        if(!file.exists() || file.length() == 0) return new ArrayList<>();
        try{
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, StatsData.class));
        } catch (IOException e) {
            System.err.println("Failed to load statistics: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    private void sendStatsToPlayer(String username){
        List<StatsData> statsList = loadStatsFromFile();
        StatsData playerStats = getStatsForUser(username, statsList);
        if(playerStats == null) {
            playerStats = new StatsData(username, 0, 0, 0);
            statsList.add(playerStats);
        }
        if(playerStats != null){
            String statsJson = convertStatsToJson(playerStats);
            UserInfo user = userMap.get(username);
            user.getUserOutput().sendMessage("STATS:" + statsJson);
        }
    }

    private String convertStatsToJson(StatsData playerStats) {
        try {
            return objectMapper.writeValueAsString(playerStats);
        } catch (IOException e) {
            System.err.println("Error converting player stats to JSON: " + e.getMessage());
            return "ERROR";
        }
    }

    private void sendBestPlayerToStats(String username){
        List<StatsData> statsList = loadStatsFromFile();
        statsList.sort((stats1, stats2) ->{
            int score1 = stats1.getWins() * 3 + stats1.getDraws();
            int score2 = stats2.getWins() * 3 + stats2.getDraws();
            return Integer.compare(score2, score1);
        });
        List<StatsData> topPlayers = statsList.stream().limit(10).toList();
        String topPlayersJson = topPlayers.stream()
                .map(this::convertStatsToJson)
                .collect(Collectors.joining(","));

        UserInfo user = userMap.get(username);
        if (user != null) {
            user.getUserOutput().sendMessage("BESTPLAYERS:" + topPlayersJson);
        } else {
            System.out.println("User not found: " + username);
        }
    }

    private void handleLogin(String username, UserInfo temp, Runnable mainListener) {
        temp.setUsername(username);
        temp.getUserOutput().sendMessage("ALLOWED");
        userMap.put(username, temp);
        Thread listener = new Thread(mainListener);
        listener.setDaemon(true);
        listenerThreads.add(listener);
        listener.start();
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
        File file = new File(LOGINDATAFILEPATH);
        if(!file.exists() || file.length() == 0) return new ArrayList<>();
        try {
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, LoginData.class));
        } catch (IOException e) {
            System.err.println("loadUsersFromFile" + e.getMessage());
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
            objectMapper.writeValue(new File(LOGINDATAFILEPATH), users);
        } catch (IOException e) {
            System.err.println("saveUsersToFile" + e.getMessage());
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
        broadCasting.interrupt();
        for (Thread thread: listenerThreads) thread.interrupt();
        for (UserInfo reader : userMap.values()) reader.closeConnection();
        userMap.clear();
        listenerThreads.clear();
        if (serverSocket != null && !serverSocket.isClosed())
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("stopAll exception" + e.getMessage());
            }
        System.exit(0);
    }
    private void stopThisUser(UserInfo userServed) {
        userServed.closeConnection();
        userMap.remove(userServed.getUsername());
    }
}
