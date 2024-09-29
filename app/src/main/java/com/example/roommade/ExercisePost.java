package com.example.roommade;

public class ExercisePost {
    private String title;
    private String content;
    private String userId;
    private long timestamp;

    public ExercisePost() {}

    public ExercisePost(String title, String content, String userId, long timestamp) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getUserId() { return userId; }
    public long getTimestamp() { return timestamp; }
}

