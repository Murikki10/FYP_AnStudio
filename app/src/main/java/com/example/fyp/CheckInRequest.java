package com.example.fyp;

public class CheckInRequest {
    private int registration_id; // 報名記錄的 ID

    // Constructor
    public CheckInRequest(int registration_id) {
        this.registration_id = registration_id;
    }

    // Getters and Setters
    public int getRegistrationId() {
        return registration_id;
    }

    public void setRegistrationId(int registration_id) {
        this.registration_id = registration_id;
    }
}