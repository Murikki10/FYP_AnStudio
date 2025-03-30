package com.example.fyp;

import java.util.List;

public class PostRequest {
    private int boardId;
    private String title;
    private String content;
    private List<Integer> tags;

    private int userId;

    public PostRequest(int boardId, String title, String content, List<Integer> tags, int userId) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.userId = userId; // 初始化 userId
    }

    public int getUserId() {
        return userId; // Getter for userId
    }

    public void setUserId(int userId) {
        this.userId = userId; // Setter for userId
    }
    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }
}