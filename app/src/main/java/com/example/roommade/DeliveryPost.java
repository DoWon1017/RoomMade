package com.example.roommade;

public class DeliveryPost {
    private String title;
    private String remainingTime;
    private long timestamp; // 타임스탬프 추가

    public DeliveryPost(String title, String remainingTime, long timestamp) { // 생성자 수정
        this.title = title;
        this.remainingTime = remainingTime;
        this.timestamp = timestamp;
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
}

