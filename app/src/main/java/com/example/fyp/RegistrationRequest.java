package com.example.fyp;

public class RegistrationRequest {
    private String form_data; // 修改為後端期待的字段名稱

    // Getter 和 Setter
    public String getFormData() {
        return form_data;
    }

    public void setFormData(String form_data) {
        this.form_data = form_data;
    }
}