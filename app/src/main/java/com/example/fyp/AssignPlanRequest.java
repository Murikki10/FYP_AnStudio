package com.example.fyp;
public class AssignPlanRequest {
    private int planId;
    private String userId;

    public AssignPlanRequest(int planId, String userId) {
        this.planId = planId;
        this.userId = userId;
    }

    // Getters and Setters
    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}