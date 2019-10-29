package com.example.jarvis.Reminder;

import android.content.Intent;

public class Alarm {
    private String hour;
    private String minute;
    private Integer isEveryday;

    private Integer isDeleted;
    private Integer isIgnored;


    public Alarm(String hour, String minute, Integer isEveryday, Integer isDeleted, Integer isIgnored) {
        this.hour = hour;
        this.minute = minute;
        this.isEveryday = isEveryday;
        this.isDeleted = isDeleted;
        this.isIgnored = isIgnored;
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

    public Integer getIsEveryday() {
        return isEveryday;
    }

    public void setIsEveryday(Integer isEveryday) {
        this.isEveryday = isEveryday;
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
}
