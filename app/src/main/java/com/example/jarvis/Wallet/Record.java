package com.example.jarvis.Wallet;

public class Record {
    private String title, description;
    private String date, amount;
    private Integer type, userId;

    public Record(Integer userId, String title, String description, String date,  Integer type, String amount) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.amount = amount;
        this.type = type;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
