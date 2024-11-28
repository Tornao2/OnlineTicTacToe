package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

public class Listener{
    private BufferedReader input;

    public void setInput(Socket socket) {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException _) {
            System.err.println("setInput");
        }
    }
    public void closeInput() {
        try {
            if (input != null) input.close();
        } catch (IOException _) {
            System.err.println("closeInput");
        }
    }
    public String receiveMessage() {
        try {
            return input.readLine();
        } catch (SocketException _) {
            return "SOCKETERROR";
        } catch (SocketTimeoutException _) {
            return null;
        } catch (IOException _) {
            System.err.println("receiveMessage");
            return null;
        }
    }
}
