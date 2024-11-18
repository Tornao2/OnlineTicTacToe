package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

public class Network {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private boolean isServer;
    private ServerSocket serverSocket;
    public Network(boolean isServer) {
        this.isServer = isServer;
        try {
            if (isServer) {
                serverSocket = new ServerSocket(12345);
                System.out.println("Server loading...");
                socket = serverSocket.accept();
                System.out.println("Client connected");
            } else {
                socket = new Socket("localhost", 12345);
                System.out.println("Connected to server");
            }
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String message) {
        output.println(message);
    }
    public String receiveMessage() {
        try {
            return input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            if (isServer && serverSocket != null) serverSocket.close();
            System.out.println("Connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
