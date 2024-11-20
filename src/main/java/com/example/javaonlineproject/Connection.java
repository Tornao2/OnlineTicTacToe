package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

public class Connection {
    private boolean isServer;
    private BufferedReader input;
    private PrintWriter output;
    private Socket socket;
    private ServerSocket serverSocket;
    public Connection(boolean isServer) {
        this.isServer = isServer;
        int triedConnecting = 0;
        boolean connected = false;
        try {
            if (isServer) {
                while (!connected) {
                    serverSocket = new ServerSocket(12345);
                    System.out.println("Server loading...");
                    socket = serverSocket.accept();
                    System.out.println("Client connected");
                }
            } else {
                while (!connected) {
                    try {
                        triedConnecting++;
                        socket = new Socket("localhost", 12345);
                        System.out.println("Connected to server");
                        connected = true;
                    } catch (IOException e) {
                        if(triedConnecting > 5) {
                            return;
                        }
                        System.out.println("Waiting for server...");
                        Thread.sleep(1000); // Wait before retrying
                    }
                }
            }
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
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
}
