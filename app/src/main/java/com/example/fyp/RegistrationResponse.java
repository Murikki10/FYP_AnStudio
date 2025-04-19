package com.example.fyp;

public class RegistrationResponse {
    private int registration_id;
    private int user_id;
    private String form_data; // JSON 格式的表單數據
    private boolean checked_in;
    private String checked_in_at;

    // Getters and Setters
    public int getRegistrationId() {
        return registration_id;
    }

    public void setRegistrationId(int registration_id) {
        this.registration_id = registration_id;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getFormData() {
        return form_data;
    }

    public void setFormData(String form_data) {
        this.form_data = form_data;
    }

    public boolean isCheckedIn() {
        return checked_in;
    }

    public void setCheckedIn(boolean checked_in) {
        this.checked_in = checked_in;
    }

    public String getCheckedInAt() {
        return checked_in_at;
    }

    public void setCheckedInAt(String checked_in_at) {
        this.checked_in_at = checked_in_at;
    }
}