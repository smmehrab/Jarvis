package com.example.jarvis.Reminder;

public class Alarm {

    private String hour;
    private String minute;
    private Integer isEveryday;
    private Integer status;

    public Alarm(){

    }

    public Alarm(String hour, String minute, Integer isEveryday, Integer status) {
        this.hour = hour;
        this.minute = minute;
        this.isEveryday = isEveryday;
        this.status = status;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
