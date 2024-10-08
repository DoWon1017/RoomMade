package com.example.roommade;

public class FreeBoardPost {
    private String title;
    private String content;
    private String userId;
    private long timestamp;
    private String postId;

    // 기본 생성자 (매개변수 없음)
    public FreeBoardPost() {
        // Firestore 역직렬화에 필요
    }

    public FreeBoardPost(String title, String content, String userId, long timestamp, String postId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
        this.postId = postId;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getUserId() { return userId; }
    public long getTimestamp() { return timestamp; }
    public String getPostId() { return postId; }
}
