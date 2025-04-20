package com.example.fyp;

public class RegisterRequest {
    private String userName; // 用戶名
    private String email;    // 電子郵件
    private String userPw;   // 密碼

    // 構造函數
    public RegisterRequest(String userName, String email, String userPw) {
        this.userName = userName;
        this.email = email;
        this.userPw = userPw;
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }
}