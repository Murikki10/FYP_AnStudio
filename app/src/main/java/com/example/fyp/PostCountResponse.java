package com.example.fyp;

public class PostCountResponse {
    private boolean success;         // 是否請求成功
    private PostCountData data;      // 對應後端的 data 部分

    // Getter 和 Setter
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public PostCountData getData() {
        return data;
    }

    public void setData(PostCountData data) {
        this.data = data;
    }

    // 嵌套類，用於解析 data
    public static class PostCountData {
        private int posts_count; // 對應後端的 posts_count

        public int getPostsCount() {
            return posts_count;
        }

        public void setPostsCount(int posts_count) {
            this.posts_count = posts_count;
        }
    }
}