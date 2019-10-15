package com.example.jarvis.Wallet;

public class WalletDetails {
    private String title, description;
    private String date;
    private Integer type, userId;

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

    public WalletDetails(String title, String description, String date, Integer type, Integer userId) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.userId = userId;
    }
}
