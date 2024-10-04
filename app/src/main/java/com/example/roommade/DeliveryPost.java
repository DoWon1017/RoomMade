package com.example.roommade;

public class DeliveryPost {
    private String title;
    private String remainingTime;
    private long timestamp;
    private String userId;



    public DeliveryPost(String title, String remainingTime, long timestamp, String userId) {
        this.title = title;
        this.remainingTime = remainingTime;
        this.timestamp = timestamp;
        this.userId = userId; // 생성자에서 초기화
    }

    public String getTitle() {
        return title;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }
}

