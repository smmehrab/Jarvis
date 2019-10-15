package com.example.jarvis.Util;

public class CustomSpinnerItem {

    private String spinnerText;
    private int spinnerImage;

    public CustomSpinnerItem(String spinnerText, int spinnerImage) {
        this.spinnerText = spinnerText;
        this.spinnerImage = spinnerImage;
    }

    public String getSpinnerText() {
        return spinnerText;
    }

    public void setSpinnerText(String spinnerText) {
        this.spinnerText = spinnerText;
    }

    public int getSpinnerImage() {
        return spinnerImage;
    }

    public void setSpinnerImage(int spinnerImage) {
        this.spinnerImage = spinnerImage;
    }
}
