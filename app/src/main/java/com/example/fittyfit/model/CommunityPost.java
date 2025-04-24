package com.example.fittyfit.model;

public class CommunityPost {
    private String userName;
    private String content;
    private int imageResId;
    // Add fields for profile image URL/resource, post image URL/resource, timestamp, etc. later

    public CommunityPost(String userName, String content, int imageResId) {
        this.userName = userName;
        this.content = content;
        this.imageResId = imageResId;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public int getImageResId() {
        return imageResId;
    }

    // Add getters for other fields
} 