package com.example.roommade;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private String id;
    private String author;
    private String content;
    private long timestamp;
    private String parentId;
    private List<Comment> replies;

    public Comment() {
        replies = new ArrayList<>();
    }

    public Comment(String id, String author, String content, long timestamp) {
        this(id, author, content, timestamp, null);
    }

    public Comment(String id, String author, String content, long timestamp, String parentId) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.replies = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public String getParentId() { return parentId; }
    public List<Comment> getReplies() { return replies; }

    public void addReply(Comment reply) {
        replies.add(reply);
    }
}

