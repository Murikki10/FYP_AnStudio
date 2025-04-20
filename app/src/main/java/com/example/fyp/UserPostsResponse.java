package com.example.fyp;

import java.util.List;

public class UserPostsResponse {
    private boolean success; // 是否請求成功
    private List<Post> data; // 帖子數據

    // Getter 和 Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Post> getData() {
        return data;
    }

    public void setData(List<Post> data) {
        this.data = data;
    }
}