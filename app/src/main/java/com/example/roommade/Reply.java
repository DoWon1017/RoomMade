package com.example.roommade;

public class Reply {
    private String id, authorId, content;
    private long timestamp;
    private int anonymousNumber;

    public Reply() {}

    public Reply(String id, String authorId, String content, long timestamp) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }

    public void setId(String id) { this.id = id; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
