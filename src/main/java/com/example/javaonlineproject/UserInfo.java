package com.example.javaonlineproject;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Klasa reprezentująca informacje o użytkowniku, w tym nazwę użytkownika oraz jego połączenie sieciowe.
 */
public class UserInfo {

    /**
     * Nazwa użytkownika.
     */
    private String username;

    /**
     * Gniazdo sieciowe użytkownika.
     */
    private Socket userSocket;

    /**
     * Obiekt odpowiedzialny za wysyłanie danych użytkownika.
     */
    private final Sender userOutput = new Sender();

    /**
     * Obiekt odpowiedzialny za odbieranie danych użytkownika.
     */
    private final Listener userInput = new Listener();

    /**
     * Pobiera nazwę użytkownika.
     *
     * @return nazwa użytkownika
     */
    public String getUsername() {
        return username;
    }

    /**
     * Ustawia nazwę użytkownika.
     *
     * @param username nowa nazwa użytkownika
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Pobiera gniazdo sieciowe użytkownika.
     *
     * @return obiekt {@link Socket} użytkownika
     */
    public Socket getUserSocket() {
        return userSocket;
    }

    /**
     * Ustawia gniazdo sieciowe użytkownika i konfiguruje jego czas oczekiwania.
     *
     * @param usersocket nowe gniazdo sieciowe
     */
    public void setUserSocket(Socket usersocket) {
        this.userSocket = usersocket;
        if (usersocket == null) return;
        try {
            userSocket.setSoTimeout(250);
        } catch (SocketException e) {
            System.err.println("setUserSocket" + e.getMessage());
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (userSocket != null && !userSocket.isClosed()) closeConnection();
        }));
    }

    /**
     * Ustawia obiekt odpowiedzialny za wysyłanie danych użytkownika.
     *
     * @param socket gniazdo sieciowe do wysyłania danych
     */
    public void setUserOutput(Socket socket) {
        userOutput.setOutput(socket);
    }

    /**
     * Ustawia obiekt odpowiedzialny za odbieranie danych użytkownika.
     *
     * @param socket gniazdo sieciowe do odbierania danych
     */
    public void setUserInput(Socket socket) {
        userInput.setInput(socket);
    }

    /**
     * Pobiera obiekt odpowiedzialny za wysyłanie danych użytkownika.
     *
     * @return obiekt {@link Sender}
     */
    public Sender getUserOutput() {
        return userOutput;
    }

    /**
     * Pobiera obiekt odpowiedzialny za odbieranie danych użytkownika.
     *
     * @return obiekt {@link Listener}
     */
    public Listener getUserInput() {
        return userInput;
    }

    /**
     * Zamknięcie połączenia użytkownika, w tym zamknięcie gniazda sieciowego, wejścia i wyjścia.
     */
    public void closeConnection() {
        if (userSocket != null) {
            try {
                userSocket.close();
            } catch (IOException _) {
                return;
            }
        }
        userSocket = null;
        userInput.closeInput();
        userOutput.closeOutput();
    }
}



