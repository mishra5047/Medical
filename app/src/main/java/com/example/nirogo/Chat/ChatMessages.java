package com.example.nirogo.Chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessages {

    String messageText;
    String messageUser;
    long messageTime;

    public ChatMessages() {
    }

    public ChatMessages(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
