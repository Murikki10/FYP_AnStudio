package com.example.fyp;

import java.util.List;

public class PostRequest {
    private int boardId;
    private String title;
    private String content;
    private List<Integer> tags; // 標籤 ID 列表
    private String imageUrl; // 單一圖片 URL
    private int userId; // 用戶 ID

    // 構造函數，支持 imageUrl 初始化
    public PostRequest(int boardId, String title, String content, List<Integer> tags, String imageUrl, int userId) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;

        // 默認處理 tags 為空的情況
        if (tags == null) {
            this.tags = List.of(); // 默認為空列表
        } else {
            this.tags = tags;
        }

        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    // Getter 和 Setter 方法
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}