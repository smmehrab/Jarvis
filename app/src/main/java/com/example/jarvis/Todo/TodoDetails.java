package com.example.jarvis.Todo;

public class TodoDetails {
    private String description, title;
    private Integer reminderState, userId;
    private String date;
    private String time;

    public TodoDetails(String description, String title, Integer reminderState, Integer userId, String date, String time) {
        this.description = description;
        this.title = title;
        this.reminderState = reminderState;
        this.userId = userId;
        this.date = date;
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReminderState() {
        return reminderState;
    }

    public void setReminderState(Integer reminderState) {
        this.reminderState = reminderState;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
