package com.example.jarvis;

import java.util.Date;

public class TodoDetails {
    private String date, time;
    private String title, desc, priority;


   /* public TodoDetails(String date, String title, String desc, String priority){
        this.date = date;
        this.title = title;
        this.desc = desc;
        this.priority = priority;
    }

    public TodoDetails(String date, String title, String desc, String time, String priority){
        this.date = date;
        this.title = title;
        this.desc = desc;
        this.time = time;
        this.priority = priority;
    }*/

    public String getDate() {
        return date;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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

}
