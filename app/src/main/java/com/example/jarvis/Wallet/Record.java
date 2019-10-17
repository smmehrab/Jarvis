package com.example.jarvis.Wallet;

public class Record {
    private Integer userId;
    private String title, description;
    private String year, month, day;
    private Integer type;
    private String amount;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Record(Integer userId, String title, String description, String year, String month, String day, Integer type, String amount) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.type = type;
        this.amount = amount;
    }
}
