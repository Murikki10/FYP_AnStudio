package com.example.fyp;

public class RegistrationRequest {
    private String form_data; // JSON 格式的表單數據

    // Constructor
    public RegistrationRequest(String form_data) {
        this.form_data = form_data;
    }

    // Getter 和 Setter
    public String getFormData() {
        return form_data;
    }

    public void setFormData(String form_data) {
        this.form_data = form_data;
    }
}