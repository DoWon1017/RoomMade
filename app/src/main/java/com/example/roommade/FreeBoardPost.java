package com.example.roommade;

public class FreeBoardPost {
    private String title;
    private String content;
    private String userId;
    private long timestamp;
    private String postId;
    private boolean isSelected = false; // 체크박스 상태 저장

    // 기본 생성자 (매개변수 없음)
    public FreeBoardPost() {
        // Firestore 역직렬화에 필요
    }

    // 생성자
    public FreeBoardPost(String title, String content, String userId, long timestamp, String postId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
        this.postId = postId;
        this.isSelected = false; // 기본값 false
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

    // 체크박스 상태를 반환하는 메서드
    public boolean isSelected() {
        return isSelected;
    }

    // 체크박스 상태를 설정하는 메서드
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}
