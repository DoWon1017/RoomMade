package com.example.roommade;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPost {
    private String postId;
    private String title;
    private String remainingTime;
    private long timestamp;
    private String userId;
    private int maxParticipants;
    private int currentParticipants;
    private boolean isActive;
    private List<String> participantIds;

    // Constructor
    public DeliveryPost(String postId, String title, String remainingTime, long timestamp,
                        String userId, int maxParticipants, int currentParticipants,
                        boolean isActive, List<String> participantIds) {
        this.postId = postId;
        this.title = title;
        this.remainingTime = remainingTime;
        this.timestamp = timestamp;
        this.userId = userId;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
        this.isActive = isActive;
        this.participantIds = participantIds != null ? participantIds : new ArrayList<>(); // null 체크 및 초기화
    }

    public String getPostId() {
        return postId;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }
}
