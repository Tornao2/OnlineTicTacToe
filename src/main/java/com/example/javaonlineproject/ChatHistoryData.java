package com.example.javaonlineproject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Klasa reprezentująca dane historii czatu.
 * Zawiera informacje o nadawcy, odbiorcy oraz treści wiadomości.
 */
public class ChatHistoryData {
    /**
     * Nadawca wiadomości.
     */
    private String sender;
    /**
     * Odbiorca wiadomości.
     */
    private String reciver;
    /**
     * Treść wiadomości.
     */
    private String message;
    /**
     * Konstruktor klasy ChatHistoryData.
     *
     * @param sender  nadawca wiadomości
     * @param reciver odbiorca wiadomości
     * @param message treść wiadomości
     */
    @JsonCreator
    public ChatHistoryData(@JsonProperty("sender") String sender,
                           @JsonProperty("reciver") String reciver,
                           @JsonProperty("message") String message) {
        this.sender = sender;
        this.reciver = reciver;
        this.message = message;
    }
    /**
     * Pobiera nadawcę wiadomości.
     *
     * @return nadawca wiadomości
     */
    public String getSender() {
        return sender;
    }
    /**
     * Ustawia nadawcę wiadomości.
     *
     * @param sender nadawca wiadomości
     */
    public void setSender(String sender) {
        this.sender = sender;
    }
    /**
     * Pobiera odbiorcę wiadomości.
     *
     * @return odbiorca wiadomości
     */
    public String getReciver() {
        return reciver;
    }
    /**
     * Ustawia odbiorcę wiadomości.
     *
     * @param reciver odbiorca wiadomości
     */
    public void setReciver(String reciver) {
        this.reciver = reciver;
    }
    /**
     * Pobiera treść wiadomości.
     *
     * @return treść wiadomości
     */
    public String getMessage() {
        return message;
    }
    /**
     * Ustawia treść wiadomości.
     *
     * @param message treść wiadomości
     */
    public void setMessage(String message) {
        this.message = message;
    }
}


