package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

public class Listener{
    private BufferedReader input;

    public void setInput(Socket socket) {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void closeInput() {
        try {
            if (input != null) input.close();
        } catch (IOException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return null;
        }
    }
}
