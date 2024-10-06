package com.example.roommade;

import java.util.List;

public class ExercisePost {
    private String postId;
    private String title;
    private String content;
    private String userId;
    private long timestamp;
    private int maxParticipants;
    private int currentParticipants;
    private List<String> participantIds;

    public ExercisePost() {}

    public ExercisePost(String postId, String title, String content, String userId, long timestamp, int maxParticipants, int currentParticipants, List<String> participantIds) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
        this.participantIds = participantIds;
    }

    public String getPostId() { return postId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getUserId() { return userId; }
    public long getTimestamp() { return timestamp; }
    public int getMaxParticipants() { return maxParticipants; }
    public int getCurrentParticipants() { return currentParticipants; }
    public List<String> getParticipantIds() { return participantIds; }

    public void setCurrentParticipants(int currentParticipants) { this.currentParticipants = currentParticipants; }
    public void setParticipantIds(List<String> participantIds) { this.participantIds = participantIds; }
}
