package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

/**
 * Klasa {@code Sender} odpowiada za wysyłanie wiadomości do serwera lub klienta
 * za pomocą obiektu {@link Socket}. Wykorzystuje {@link PrintWriter} do zapisu wiadomości
 * w strumieniu wyjściowym gniazda.
 *
 * <p>Klasa udostępnia metody do ustawiania strumienia wyjściowego, wysyłania wiadomości
 * oraz zamykania strumienia wyjściowego.</p>
 */
public class Sender {
    /**
     * Obiekt {@link PrintWriter}, który służy do wysyłania wiadomości za pomocą
     * strumienia wyjściowego gniazda.
     */
    private PrintWriter output;
    /**
     * Ustawia strumień wyjściowy gniazda, umożliwiając wysyłanie wiadomości.
     *
     * @param socket gniazdo, którego strumień wyjściowy będzie używany do wysyłania wiadomości
     */
    public void setOutput(Socket socket) {
        try {
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Błąd w setOutput: " + e.getMessage());
        }
    }
    /**
     * Wysyła wiadomość za pomocą strumienia wyjściowego gniazda.
     *
     * @param message wiadomość, która ma zostać wysłana
     * @throws NullPointerException jeśli strumień wyjściowy nie został zainicjowany
     */
    public void sendMessage(String message) {
        if (output != null) {
            output.println(message);
        } else {
            throw new NullPointerException("Strumień wyjściowy nie został zainicjowany. Wywołaj najpierw setOutput().");
        }
    }
    /**
     * Zamyka obiekt {@link PrintWriter} używany do wysyłania wiadomości.
     *
     * <p>Jeśli strumień wyjściowy jest już zamknięty, metoda nie wykonuje żadnych operacji.</p>
     */
    public void closeOutput() {
        if (output != null) {
            output.close();
        }
    }
}


