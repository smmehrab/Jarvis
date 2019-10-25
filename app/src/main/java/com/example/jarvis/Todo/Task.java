package com.example.jarvis.Todo;

public class Task {
    private Integer userId;
    private String title;
    private String description;

    private String year, month, day;
    private String hour, minute;

    private Integer reminderState;
    private Integer checked;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public Integer getReminderState() {
        return reminderState;
    }

    public void setReminderState(Integer reminderState) {
        this.reminderState = reminderState;
    }

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public Task(Integer userId, String title, String description, String year, String month, String day, String hour, String minute, Integer reminderState) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.reminderState = reminderState;
        this.checked = 0;
    }
}
