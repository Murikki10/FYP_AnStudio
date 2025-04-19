package com.example.fyp;

import java.util.List;

public class PostRequest {
    private int boardId;
    private String title;
    private String content;
    private List<Integer> tags; // 標籤 ID 列表
    private String imageUrl;    // 單一圖片 URL

    public PostRequest(int boardId, String title, String content, List<Integer> tags, String imageUrl) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.tags = tags != null ? tags : List.of(); // 默認為空列表
        this.imageUrl = imageUrl;
    }

    // Getters 和 Setters
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}