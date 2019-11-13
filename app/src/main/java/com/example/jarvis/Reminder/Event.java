package com.example.jarvis.Reminder;//package com.example.jarvis.Reminder;

public class Event {

    private String title;
    private String description;
    private String type;

    private String year;
    private String month;
    private String day;

    private String hour;
    private String minute;

    private int isDeleted;
    private int isIgnored;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public int getIsIgnored() {
        return isIgnored;
    }

    public void setIsIgnored(int isIgnored) {
        this.isIgnored = isIgnored;
    }

    public Event(String title, String description, String type, String year, String month, String day, String hour, String minute, int isDeleted, int isIgnored) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.isDeleted = isDeleted;
        this.isIgnored = isIgnored;
    }
}
