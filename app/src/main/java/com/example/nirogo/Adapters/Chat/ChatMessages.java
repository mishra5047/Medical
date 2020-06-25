package com.example.nirogo.Adapters.Chat;

import java.util.Date;

public class ChatMessages {

    String sender;
    String receiver;
    String message;

    public ChatMessages() {
    }

    public ChatMessages(String sender,String message, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
