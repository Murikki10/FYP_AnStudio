package com.example.fyp;

public class FollowRequest {
    private boolean follow;

    public FollowRequest(boolean follow) {
        this.follow = follow;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}