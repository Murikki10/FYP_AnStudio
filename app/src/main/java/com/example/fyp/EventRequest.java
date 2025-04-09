package com.example.fyp;

public class EventRequest {
    private String title;
    private String description;
    private String start_time;
    private String end_time;
    private int created_by; // 創建者的用戶 ID

    // Constructor
    public EventRequest(String title, String description, String start_time, String end_time, int created_by) {
        this.title = title;
        this.description = description;
        this.start_time = start_time;
        this.end_time = end_time;
        this.created_by = created_by;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return start_time;
    }

    public void setStartTime(String start_time) {
        this.start_time = start_time;
    }

    public String getEndTime() {
        return end_time;
    }

    public void setEndTime(String end_time) {
        this.end_time = end_time;
    }

    public int getCreatedBy() {
        return created_by;
    }

    public void setCreatedBy(int created_by) {
        this.created_by = created_by;
    }
}