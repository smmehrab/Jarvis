package com.example.jarvis;

public class WalletDetails {

    private String title, desc, date, expenseType;


    public WalletDetails (String title, String desc, String expenseType, String date){
        this.title = title;
        this.desc = desc;
        this.expenseType = expenseType;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }
}
