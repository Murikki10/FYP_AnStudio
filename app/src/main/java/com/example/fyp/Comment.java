package com.example.fyp;

import com.google.gson.annotations.SerializedName;

public class Comment {
    private int commentId;
    private String content;
    @SerializedName("authorName") // 映射後端字段
    private String authorName;
    private String createdAt;

    // Constructor
    public Comment(int commentId, String content, String authorName, String createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.authorName = authorName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}