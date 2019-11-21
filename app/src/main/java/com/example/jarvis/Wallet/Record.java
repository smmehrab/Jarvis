package com.example.jarvis.Wallet;

public class Record {
    private String title;
    private String description;

    private String year, month, day;

    private Integer type;
    // Type 0 - Expense - Red
    // Type 1 - Earning - Green

    private String amount;

    private Integer isDeleted;
    private Integer isIgnored;

    private Integer syncState;

    public Record(){

    }

    public Record(String title, String description, String year, String month, String day, Integer type, String amount, Integer isDeleted, Integer isIgnored, Integer syncState) {
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.type = type;
        this.amount = amount;
        this.isDeleted = isDeleted;
        this.isIgnored = isIgnored;
        this.syncState = syncState;
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

    public Integer getSyncState() {
        return syncState;
    }

    public void setSyncState(Integer syncState) {
        this.syncState = syncState;
    }

    public Record(String title, String description, String year, String month, String day, Integer type, String amount) {
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.type = type;
        this.amount = amount;
        this.isDeleted = 0;
        this.isIgnored = 0;
        this.syncState = 0;
    }
}
