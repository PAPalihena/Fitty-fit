package com.example.fittyfit;

public class HealthTip {
    private String title;
    private String tip;
    private int imageResourceId;

    public HealthTip(String title, String tip, int imageResourceId) {
        this.title = title;
        this.tip = tip;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getTip() {
        return tip;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
} 