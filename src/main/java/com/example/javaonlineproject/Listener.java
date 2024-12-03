package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

public class Listener{
    private BufferedReader input;

    public void setInput(Socket socket) {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("setInput" + e.getMessage());
        }
    }
    public void closeInput() {
        try {
            if (input != null) input.close();
        } catch (IOException e) {
            System.err.println("closeInput" + e.getMessage());
        }
    }
    public String receiveMessage() {
        try {
            return input.readLine();
        } catch (SocketException _) {
            return "SOCKETERROR";
        } catch (SocketTimeoutException _) {
            return null;
        } catch (IOException e) {
            System.err.println("receiveMessage" + e.getMessage());
            return null;
        }
    }
}
