package com.example.fyp;

import java.util.List;

public class ApiResponse<T> {
    private List<Post> posts; // 對應後端的 "posts" 數組
    private Pagination pagination; // 對應後端的 "pagination" 對象
    private String message;
    private boolean success;
    private T data; // 泛型數據
    public List<Post> getPosts() {
        return posts;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}