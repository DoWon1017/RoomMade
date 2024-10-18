package com.example.roommade;

public class FreeBoardPost {
    private String title;
    private String content;
    private String userId;
    private long timestamp;
    private String postId;
    private String imageUrl;

    // 기본 생성자 (매개변수 없음)
    public FreeBoardPost() {
        // Firestore 역직렬화에 필요
    }

    // 생성자
    public FreeBoardPost(String title, String content, String userId, long timestamp, String postId, String imageUrl) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
        this.postId = postId;
        this.imageUrl = imageUrl;
    }

    // Getter 메서드
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
