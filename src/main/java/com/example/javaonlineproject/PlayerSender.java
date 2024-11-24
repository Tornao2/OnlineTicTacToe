package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

public class PlayerSender{
    private final PrintWriter output;
    PlayerSender(Socket socket) {
        try {
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }
}