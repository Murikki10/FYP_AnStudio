package com.example.fyp;

public class ChatMessage {
    private final String user;
    private final String content;

    public ChatMessage(String user, String content) {
        this.user = user;
        this.content = content;
    }

    public String getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }
}