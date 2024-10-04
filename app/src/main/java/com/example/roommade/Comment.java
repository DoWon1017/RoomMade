package com.example.roommade;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private String id;
    private String authorId;
    private String content;
    private long timestamp;
    private List<Reply> replies;

    public Comment(String id, String authorId, String content, long timestamp, ArrayList<Reply> replies) {
        this.id = id;
        this.authorId = authorId;
        this.content = content;
        this.timestamp = timestamp;
        this.replies = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public List<Reply> getReplies() { return replies; }

    public void setContent(String content) { this.content = content; }
    public void setReplies(List<Reply> replies) { this.replies = replies; }

}
