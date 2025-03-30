package com.example.fyp;

public class PostsResponse {
    private int postId;
    private String message;

    // Getter 和 Setter 方法
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}