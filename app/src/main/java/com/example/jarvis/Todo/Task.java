package com.example.jarvis.Todo;

public class Task {
    private String title;
    private String description;

    private String year, month, day;
    private String hour, minute;

    private Integer reminderState;
    private Integer isCompleted;
    private Integer isDeleted;
    private Integer isIgnored;
    private String updateTimestamp;

    public Task(String title, String description, String year, String month, String day, String hour, String minute, Integer reminderState, Integer isCompleted, Integer isDeleted, Integer isIgnored, String updateTimestamp) {
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.reminderState = reminderState;
        this.isCompleted = isCompleted;
        this.isDeleted = isDeleted;
        this.isIgnored = isIgnored;
        this.updateTimestamp = updateTimestamp;
    }


    public Task(String title, String description, String year, String month, String day, String hour, String minute, Integer reminderState) {
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.reminderState = reminderState;
        this.isCompleted = 0;
        this.isDeleted = 0;
        this.isIgnored = 0;

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        this.updateTimestamp = ts;
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

    public Integer getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Integer isCompleted) {
        this.isCompleted = isCompleted;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getIsIgnored() {
        return isIgnored;
    }

    public void setIsIgnored(Integer isIgnored) {
        this.isIgnored = isIgnored;
    }

    public String getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(String updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
}
