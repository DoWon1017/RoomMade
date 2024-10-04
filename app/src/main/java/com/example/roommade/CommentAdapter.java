package com.example.roommade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private CommentReplyListener replyListener;

    public CommentAdapter(List<Comment> commentList, CommentReplyListener replyListener) {
        this.commentList = commentList;
        this.replyListener = replyListener;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.authorTextView.setText(comment.getAuthor());
        holder.contentTextView.setText(comment.getContent());
        holder.timestampTextView.setText(formatDate(comment.getTimestamp()));

        holder.btnReply.setOnClickListener(v -> {
            // 대댓글 입력창 토글 로직 추가 가능
            if (holder.layoutReplyInput.getVisibility() == View.GONE) {
                holder.layoutReplyInput.setVisibility(View.VISIBLE);
            } else {
                holder.layoutReplyInput.setVisibility(View.GONE);
            }
        });

        holder.buttonSubmitReply.setOnClickListener(v -> {
            String replyContent = holder.editTextReply.getText().toString().trim();
            if (!replyContent.isEmpty()) {
                String author = comment.getAuthor().equals("작성자") ? "작성자" : "익명";
                long timestamp = System.currentTimeMillis();
                String replyId = FirebaseDatabase.getInstance().getReference().push().getKey();

                Comment reply = new Comment(replyId, author, replyContent, timestamp, comment.getId());
                comment.addReply(reply);

                DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("comments");
                commentRef.child(replyId).setValue(reply);

                holder.editTextReply.setText("");
                replyListener.onReplySubmitted(comment.getId(), author, replyContent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public interface CommentReplyListener {
        void onReplySubmitted(String commentId, String author, String content);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView, contentTextView, timestampTextView, btnReply;
        EditText editTextReply;
        Button buttonSubmitReply;
        View layoutReplyInput;

        public CommentViewHolder(View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.textViewCommentAuthor);
            contentTextView = itemView.findViewById(R.id.textViewCommentContent);
            timestampTextView = itemView.findViewById(R.id.textViewCommentTimestamp);
            btnReply = itemView.findViewById(R.id.btnReply);
            editTextReply = itemView.findViewById(R.id.editTextReply);
            buttonSubmitReply = itemView.findViewById(R.id.buttonSubmitReply);
            layoutReplyInput = itemView.findViewById(R.id.layoutReplyInput);
        }
    }

    private String formatDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
}
