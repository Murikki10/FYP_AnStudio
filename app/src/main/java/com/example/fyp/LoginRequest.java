package com.example.fyp;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("userName")
    private String userName;

    @SerializedName("userPw")
    private String userPw;

    public LoginRequest(String userName, String userPw) {
        this.userName = userName;
        this.userPw = userPw;
    }
}