package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

public class Connection {
    private Runnable onConnectionSuccess;
    private final boolean isServer;
    private BufferedReader input;
    private PrintWriter output;
    private Socket socket;
    private ServerSocket serverSocket;

    public Connection(boolean isServer) {
        this.isServer = isServer;
    }
    public void start(){
        int triedConnecting = 0;
        boolean connected = false;
        try {
            if (isServer) {
                try {
                    serverSocket = new ServerSocket(12345);
                    serverSocket.setSoTimeout(5000);
                } catch (BindException e) {
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    serverSocket.close();
                    return;
                }
            } else {
                while (!connected) {
                    try {
                        triedConnecting++;
                        socket = new Socket("localhost", 12345);
                        connected = true;
                    } catch (IOException e) {
                        if(triedConnecting > 5) {
                            return;
                        }
                        Thread.sleep(1000);
                    }
                }
            }
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            onConnectionSuccess.run();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void closeConnection() {
        if (!socket.isClosed()) {
            output.println("CLOSING");
        }
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
            if (serverSocket != null) serverSocket.close();
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
        } catch (SocketException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setOnConnectionSuccess(Runnable onConnectionSuccess) {
        this.onConnectionSuccess = onConnectionSuccess;
    }
    public boolean getIsServer() {
        return this.isServer;
    }
}
