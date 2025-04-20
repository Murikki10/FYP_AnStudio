package com.example.fyp;

import com.google.gson.annotations.SerializedName;

public class Board {

    @SerializedName("boardId")
    private int boardId;

    @SerializedName("boardName")
    private String boardName;

    @SerializedName("description")
    private String description;

    @SerializedName("followersCount")
    private int followersCount;

    @SerializedName("isFollowed")
    private int isFollowed; // 使用 int 來映射數字類型的 isFollowed
    public Board(int boardId, String boardName) {
        this.boardId = boardId;
        this.boardName = boardName;
    }

    // Getters 和 Setters
    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public boolean isFollowed() {
        return isFollowed == 1; // 將 int 轉換為 boolean
    }

    public void setFollowed(boolean followed) {
        this.isFollowed = followed ? 1 : 0; // 將布爾值轉換為 int
    }

    public void setIsFollowed(int isFollowed) {
        this.isFollowed = isFollowed;
    }

    @Override
    public String toString() {
        return boardName; // 返回分區名稱
    }
}