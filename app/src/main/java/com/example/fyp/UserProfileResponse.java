package com.example.fyp;

public class UserProfileResponse {

    private boolean success;
    private UserProfile data;
    private int userId;

    public int getUserId() {
        return userId;
    }
    public boolean isSuccess() {
        return success;
    }

    public UserProfile getData() {
        return data;
    }
    private String userName;
    private String email;
    private String avatarUrl;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private UserProfile user;

    // Getters and setters
    public String getUserName() { return userName; }
    public String getEmail() { return email; }

    public UserProfile getUser() {return user;}
    public String getAvatarUrl() { return avatarUrl; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getGender() { return gender; }}

class UserProfile {

    private int userId;
    private String userName;
    private String bio;
    private String email;
    private String phone;
    private String avatarUrl;
    private int followersCount;
    private int followingCount;
    private int postsCount;
    private String firstName; // 添加字段
    private String lastName;  // 添加字段
    private String gender;    // 添加字段
    private String visibility; // 新增字段
    private String backgroundUrl; // 新增字段
    private String createdAt; // 新增字段

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getBio() {
        return bio;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getPostsCount() {
        return postsCount;
    }
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getVisibility() { // 新增 getter
        return visibility;
    }

    public String getBackgroundUrl() { // 新增 getter
        return backgroundUrl;
    }

    public String getCreatedAt() { // 新增 getter
        return createdAt;
    }
}