package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

public class Connection {
    private final boolean isServer;
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
                try {
                    serverSocket = new ServerSocket(12345);
                    System.out.println("Server loading..."); // Debugging
                    serverSocket.setSoTimeout(10000);
                } catch (BindException e) {
                    System.out.println("Server is already running on this port. Choose client mode.");
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    socket = serverSocket.accept();
                    System.out.println("Client connected"); // Debugging
                } catch (SocketTimeoutException e) {
                    System.out.println("No client connected within the timeout period."); // Debugging
                    serverSocket.close();
                    return;
                }
            } else {
                while (!connected) {
                    try {
                        triedConnecting++;
                        socket = new Socket("localhost", 12345);
                        System.out.println("Connected to server"); // Debugging
                        connected = true;
                    } catch (IOException e) {
                        if(triedConnecting > 5) {
                            System.out.println("Didn't connect to server"); // Debugging
                            return;
                        }
                        Thread.sleep(1000);
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
            System.out.println("Connection closed"); // Debugging
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
