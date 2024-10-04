package com.example.roommade;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private HashMap<String, String> anonymousMap = new HashMap<>();
    private int anonymousCount = 1;
    private CommentDeleteListener deleteListener;
    private ReplyClickListener replyClickListener;
    private FreeBoardPost post;
    private ReplyDeleteListener replyDeleteListener;
    private String currentUserId;  // 현재 로그인한 사용자 ID

    public CommentAdapter(List<Comment> commentList, FreeBoardPost post, CommentDeleteListener deleteListener,
                          ReplyClickListener replyClickListener, ReplyDeleteListener replyDeleteListener,
                          String currentUserId) {
        this.commentList = commentList;
        this.post = post;
        this.deleteListener = deleteListener;
        this.replyClickListener = replyClickListener;
        this.replyDeleteListener = replyDeleteListener;
        this.currentUserId = currentUserId;
    }


    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        String authorDisplayName = getAuthorDisplayName(comment.getAuthorId());

        holder.authorTextView.setText(authorDisplayName);
        holder.contentTextView.setText(comment.getContent());
        if (comment.getContent().equals("삭제된 댓글입니다.")) {
            holder.contentTextView.setText(comment.getContent());
        }
        holder.timestampTextView.setText(formatDate(comment.getTimestamp()));
        if (comment.getAuthorId().equals(currentUserId)) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("삭제 확인")
                        .setMessage("이 댓글을 삭제하시겠습니까?")
                        .setPositiveButton("예", (dialog, which) -> {
                            deleteListener.onDeleteComment(comment, position);
                        })
                        .setNegativeButton("아니요", null)
                        .show();
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.btnReply.setVisibility(View.VISIBLE);
        holder.btnReply.setOnClickListener(v -> {
            if (replyClickListener != null) {
                replyClickListener.onReplyClick(comment.getId(), holder);
            }
        });
        loadReplies(holder, comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private String getAuthorDisplayName(String authorId) {
        if (authorId.equals(post.getUserId())) {
            return "작성자";
        }
        return anonymousMap.computeIfAbsent(authorId, k -> "익명" + anonymousCount++);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView, contentTextView, timestampTextView;
        Button btnDelete, btnReply;
        RecyclerView recyclerViewReplies;

        public CommentViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.textViewCommentAuthor);
            contentTextView = itemView.findViewById(R.id.textViewCommentContent);
            timestampTextView = itemView.findViewById(R.id.textViewCommentTimestamp);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnReply = itemView.findViewById(R.id.btnReply);
            recyclerViewReplies = itemView.findViewById(R.id.recyclerViewReplies);
        }
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    private void loadReplies(CommentViewHolder holder, Comment comment) {
        List<Reply> replies = comment.getReplies();
        if (replies != null && !replies.isEmpty()) {
            ReplyAdapter replyAdapter = new ReplyAdapter(replies, comment.getId(), new ReplyAdapter.OnReplyClickListener() {
                @Override
                public void onDeleteReply(Reply reply, String commentId, int position) {
                    replyDeleteListener.onDeleteReply(reply, commentId, position);
                }
            }, post, currentUserId);

            holder.recyclerViewReplies.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.recyclerViewReplies.setAdapter(replyAdapter);
            holder.recyclerViewReplies.setVisibility(View.VISIBLE);
        } else {
            holder.recyclerViewReplies.setVisibility(View.GONE);
        }
    }

    public interface CommentDeleteListener {
        void onDeleteComment(Comment comment, int position);
    }

    public interface ReplyClickListener {
        void onReplyClick(String commentId, CommentViewHolder holder);
    }

    public interface ReplyDeleteListener {
        void onDeleteReply(Reply reply, String commentId, int position);
    }

}
