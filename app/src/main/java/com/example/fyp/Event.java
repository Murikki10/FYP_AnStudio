package com.example.fyp;

import android.icu.text.SimpleDateFormat;

import java.util.Date;

public class Event {
    private int event_id;
    private String title;
    private String description;
    private String start_time;
    private String end_time;

    private String location;

    // Getters and Setters
    public int getEventId() {
        return event_id;
    }

    public void setEventId(int event_id) {
        this.event_id = event_id;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFormattedStartTime() {
        return formatDate(start_time);
    }

    public String getFormattedEndTime() {
        return formatDate(end_time);
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d, yyyy, h:mm a"); // 格式化為 "April 10, 2025, 10:00 AM"
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateString; // 如果解析失敗，返回原始日期
        }
    }
}