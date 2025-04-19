package com.example.fyp;

public class FormField {
    private int field_id;
    private String field_name;
    private String field_type; // 如 "text", "number", "date" 等
    private boolean is_required;

    // Getters and Setters
    public int getFieldId() {
        return field_id;
    }

    public void setFieldId(int field_id) {
        this.field_id = field_id;
    }

    public String getFieldName() {
        return field_name;
    }

    public void setFieldName(String field_name) {
        this.field_name = field_name;
    }

    public String getFieldType() {
        return field_type;
    }

    public void setFieldType(String field_type) {
        this.field_type = field_type;
    }

    public boolean isRequired() {
        return is_required;
    }

    public void setRequired(boolean is_required) {
        this.is_required = is_required;
    }
}