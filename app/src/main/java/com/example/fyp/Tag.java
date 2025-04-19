package com.example.fyp;

import com.google.gson.annotations.SerializedName;

public class Tag {
    @SerializedName("tagId")
    private int tagId;

    @SerializedName("tagName")
    private String tagName;

    // Getter 和 Setter 方法
    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}