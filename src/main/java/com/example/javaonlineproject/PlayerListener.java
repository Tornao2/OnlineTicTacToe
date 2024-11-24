package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

public class PlayerListener implements Runnable{
    private final BufferedReader input;
    private Socket socket;

    PlayerListener(Socket socket) {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void closeConnection() {
        try {
            if (input != null) input.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String message = receiveMessage();
            switch (message) {

            }
        }
        closeConnection();
    }
    public Socket getSocket() {
        return socket;
    }
}
