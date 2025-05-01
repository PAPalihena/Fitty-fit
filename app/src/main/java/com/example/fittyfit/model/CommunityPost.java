package com.example.fittyfit.model;

public class CommunityPost {
    private String userName;
    private String content;
    private String timeAgo;
    private int likes;
    private int comments;
    private int shares;
    private int imageResId;
    // Add fields for profile image URL/resource, post image URL/resource, timestamp, etc. later

    // Constructor for posts with image
    public CommunityPost(String userName, String content, int imageResId) {
        this.userName = userName;
        this.content = content;
        this.imageResId = imageResId;
        this.timeAgo = "Just now";
        this.likes = 0;
        this.comments = 0;
        this.shares = 0;
    }

    // Constructor for posts with all details
    public CommunityPost(String userName, String content, String timeAgo, int likes, int comments, int shares) {
        this.userName = userName;
        this.content = content;
        this.timeAgo = timeAgo;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
        this.imageResId = 0; // No image by default
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    // Add getters for other fields
} 