package com.example.roommade;

public class DeliveryPost {
    private String title;
    private String remainingTime;
    private long timestamp;
    private String userId;
    private int maxParticipants;
    private int currentParticipants;

    public DeliveryPost(String title, String remainingTime, long timestamp, String userId, int maxParticipants, int currentParticipants) {
        this.title = title;
        this.remainingTime = remainingTime;
        this.timestamp = timestamp;
        this.userId = userId;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
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

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }
}


