package com.example.roommade;

public class ExercisePost {
    private String title;
    private String content;
    private String userId;
    private long timestamp;
    private int maxParticipants;
    private int currentParticipants;

    public ExercisePost() {}

    public ExercisePost(String title, String content, String userId, long timestamp, int maxParticipants, int currentParticipants) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getUserId() { return userId; }
    public long getTimestamp() { return timestamp; }
    public int getMaxParticipants() { return maxParticipants; }
    public int getCurrentParticipants() { return currentParticipants; }
}
