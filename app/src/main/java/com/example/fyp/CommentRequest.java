package com.example.fyp;

public class CommentRequest {
    private String content; // 留言的內容

    // Constructor
    public CommentRequest(String content) {
        this.content = content;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
