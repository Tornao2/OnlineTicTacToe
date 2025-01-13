package com.example.javaonlineproject;

import java.io.*;
import java.net.*;

/**
 * Klasa Listener odpowiedzialna za obsługę wejścia z gniazda sieciowego.
 * Umożliwia ustawienie strumienia wejściowego, odbieranie wiadomości oraz
 * zamknięcie strumienia.
 */
public class Listener {
    /**
     * Buforowany strumień wejściowy do odczytu danych z gniazda.
     */
    private BufferedReader input;
    /**
     * Ustawia strumień wejściowy dla podanego gniazda.
     *
     * @param socket gniazdo sieciowe, z którego ma być odczytywany strumień danych
     */
    public void setInput(Socket socket) {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("setInput: " + e.getMessage());
        }
    }
    /**
     * Zamyka strumień wejściowy, jeśli jest otwarty.
     */
    public void closeInput() {
        try {
            if (input != null) input.close();
        } catch (IOException e) {
            System.err.println("closeInput: " + e.getMessage());
        }
    }
    /**
     * Odbiera wiadomość z ustawionego strumienia wejściowego.
     *
     * @return odebrana wiadomość jako ciąg znaków; zwraca "SOCKETERROR" w przypadku
     *         błędu gniazda, {@code null} w przypadku upłynięcia limitu czasu
     *         lub wystąpienia błędu wejścia/wyjścia
     */
    public String receiveMessage() {
        try {
            return input.readLine();
        } catch (SocketException _) {
            return "SOCKETERROR";
        } catch (SocketTimeoutException _) {
            return "timeout";
        } catch (IOException e) {
            System.err.println("receiveMessage: " + e.getMessage());
            return "SOCKETERROR";
        }
    }
}


