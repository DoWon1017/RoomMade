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
import java.util.Map;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private List<Reply> replyList;
    private String commentId;
    private HashMap<String, String> anonymousMap = new HashMap<>();
    private int anonymousCount = 1;
    private OnReplyClickListener replyClickListener;
    private FreeBoardPost post;
    private String currentUserId;

    public ReplyAdapter(List<Reply> replyList, String commentId, OnReplyClickListener replyClickListener, FreeBoardPost post, String currentUserId) {
        this.replyList = replyList;
        this.commentId = commentId;
        this.replyClickListener = replyClickListener;
        this.post = post;
        this.currentUserId = currentUserId;
    }

    @Override
    public ReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReplyViewHolder holder, int position) {
        Reply reply = replyList.get(position);
        String authorDisplayName = getAuthorDisplayName(reply.getAuthorId());

        holder.authorTextView.setText(authorDisplayName);
        holder.contentTextView.setText(reply.getContent());
        holder.timestampTextView.setText(formatDate(reply.getTimestamp()));

        if (reply.getAuthorId().equals(currentUserId)) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("삭제 확인")
                        .setMessage("이 답글을 삭제하시겠습니까?")
                        .setPositiveButton("예", (dialog, which) -> {
                            replyClickListener.onDeleteReply(reply, commentId, position);
                        })
                        .setNegativeButton("아니요", null)
                        .show();
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return replyList.size();
    }

    private String getAuthorDisplayName(String authorId) {
        if (authorId.equals(post.getUserId())) {
            return "작성자";
        }

        if (anonymousMap.containsKey(authorId)) {
            return anonymousMap.get(authorId);
        } else {
            String newNickname = "익명" + (anonymousCount++);
            anonymousMap.put(authorId, newNickname);
            return newNickname;
        }
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView, contentTextView, timestampTextView;
        Button btnDelete;

        public ReplyViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.textViewReplyAuthor);
            contentTextView = itemView.findViewById(R.id.textViewReplyContent);
            timestampTextView = itemView.findViewById(R.id.textViewReplyTimestamp);
            btnDelete = itemView.findViewById(R.id.btnDeleteReply);
        }
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public interface OnReplyClickListener {
        void onDeleteReply(Reply reply, String commentId, int position);
    }
}
