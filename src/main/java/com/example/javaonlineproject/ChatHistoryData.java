package com.example.javaonlineproject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatHistoryData {
    private String sender;
    private String reciver;
    private String message;

    @JsonCreator
    public ChatHistoryData(@JsonProperty("sender") String sender, @JsonProperty("reciver") String reciver, @JsonProperty("message") String message){
        this.sender = sender;
        this.reciver = reciver;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getReciver() {
        return reciver;
    }
    public void setReciver(String reciver) {
        this.reciver = reciver;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
