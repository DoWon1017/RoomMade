package com.example.roommade;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPost {
    private String postId;
    private String userId;
    private String title;
    private int currentParticipants;
    private int maxParticipants;
    private String remainingTime;
    private long timestamp;
    private boolean isActive;
    private List<String> participantIds;

    // 생성자
    public DeliveryPost(String postId, String userId, String title, int currentParticipants, int maxParticipants,
                        String remainingTime, long timestamp, boolean isActive, List<String> participantIds) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.currentParticipants = currentParticipants;
        this.maxParticipants = maxParticipants;
        this.remainingTime = remainingTime;
        this.timestamp = timestamp;
        this.isActive = isActive;
        this.participantIds = participantIds;
    }

    public DeliveryPost() {
        participantIds = new ArrayList<>();
    }

    // Getters and Setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(int currentParticipants) { this.currentParticipants = currentParticipants; }
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    public String getRemainingTime() { return remainingTime; }
    public void setRemainingTime(String remainingTime) { this.remainingTime = remainingTime; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
    public List<String> getParticipantIds() { return participantIds != null ? participantIds : new ArrayList<>(); }
    public void setParticipantIds(List<String> participantIds) { this.participantIds = participantIds; }
}
