package com.example.javaonlineproject;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class UserInfo {
    private String username;
    private Socket userSocket;
    private final Sender userOutput = new Sender();
    private final Listener userInput = new Listener();

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Socket getUserSocket() {
        return userSocket;
    }
    public void setUserSocket(Socket usersocket) {
        this.userSocket = usersocket;
        try {
            userSocket.setSoTimeout(1000);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
    public void setUseroutput(Socket socket) {
        userOutput.setOutput(socket);
    }
    public void setUserinput(Socket socket) {
        userInput.setInput(socket);
    }
    public Sender getUserOutput(){
        return userOutput;
    }
    public Listener getUserInput(){
        return userInput;
    }

    public void closeConnection() {
        if (userSocket != null) try {
            userSocket.close();
        } catch (IOException _) {
            return;
        }
        userInput.closeInput();
        userOutput.closeOutput();
    }
}