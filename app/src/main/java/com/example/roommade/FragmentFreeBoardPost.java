package com.example.roommade;

import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentFreeBoardPost extends Fragment {

    private FreeBoardPost post;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText editTextComment;
    private Button buttonSubmitComment;
    private FirebaseFirestore db;
    private String postId;
    private String postUserId;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freeboardpost, container, false);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user != null ? user.getUid() : null;

        Bundle args = getArguments();
        if (args != null) {
            postId = args.getString("postId");
            postUserId = args.getString("userId");
            post = new FreeBoardPost(
                    args.getString("title"),
                    args.getString("content"),
                    postUserId,
                    args.getLong("timestamp"),
                    postId
            );

            setupPostViews(view);
        }

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            FragmentFreeBoard fragmentFreeBoard = new FragmentFreeBoard();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containers, fragmentFreeBoard)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerViewComments = view.findViewById(R.id.recyclerViewComments);
        editTextComment = view.findViewById(R.id.editTextComment);
        buttonSubmitComment = view.findViewById(R.id.buttonSubmitComment);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, post, this::deleteComment, this::onReplyClick, this::deleteReply);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewComments.setAdapter(commentAdapter);

        loadComments();

        buttonSubmitComment.setOnClickListener(v -> {
            String content = editTextComment.getText().toString().trim();
            if (!content.isEmpty()) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                saveCommentToFirestore(content, timestamp);
            } else {
                Toast.makeText(getContext(), "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setupPostViews(View view) {
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewContent = view.findViewById(R.id.textViewContent);
        TextView textViewAuthor = view.findViewById(R.id.textViewAuthor);
        TextView textViewTimestamp = view.findViewById(R.id.textViewTimestamp);

        textViewTitle.setText(post.getTitle());
        textViewContent.setText(post.getContent());
        textViewAuthor.setText("익명");
        textViewTimestamp.setText(formatDate(post.getTimestamp()));
    }

    private void loadComments() {
        db.collection("freeboard_posts").document(postId).collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "댓글을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    commentList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            String id = doc.getId();
                            String authorId = doc.getString("authorId");
                            String content = doc.getString("content");
                            long timestamp = doc.getLong("timestamp");

                            Comment comment = new Comment(id, authorId, content, timestamp, new ArrayList<Reply>());


                            if (content.equals("삭제된 댓글입니다.")) {
                                comment.setContent("삭제된 댓글입니다.");
                            }

                            commentList.add(comment);
                            loadReplies(id, comment);
                        }
                    }
                    commentAdapter.notifyDataSetChanged();
                });
    }

    private void loadReplies(String commentId, Comment comment) {
        if (comment == null) {
            return;
        }

        db.collection("freeboard_posts").document(postId).collection("comments").document(commentId).collection("replies")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "답글을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Reply> replies = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            String id = doc.getId();
                            String replyAuthorId = doc.getString("authorId");
                            String replyContent = doc.getString("content");
                            long replyTimestamp = doc.getLong("timestamp");

                            Reply reply = new Reply(id, replyAuthorId, replyContent, replyTimestamp);
                            replies.add(reply);
                        }
                    }

                    if (comment.getReplies() == null) {
                        comment.setReplies(replies);
                    } else {
                        comment.getReplies().clear();
                        comment.getReplies().addAll(replies);
                    }

                    commentAdapter.notifyDataSetChanged();
                });
    }

    private void saveCommentToFirestore(String content, String timestamp) {
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("authorId", currentUserId);
        commentData.put("content", content);
        commentData.put("timestamp", Long.parseLong(timestamp));

        String commentId = db.collection("freeboard_posts").document(postId).collection("comments").document().getId();
        db.collection("freeboard_posts").document(postId).collection("comments").document(commentId)
                .set(commentData)
                .addOnSuccessListener(aVoid -> {
                    editTextComment.setText("");
                    Toast.makeText(getContext(), "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "댓글 추가에 실패했습니다.", Toast.LENGTH_SHORT).show());
    }

    private void deleteComment(Comment comment, int position) {
        comment.setContent("삭제된 댓글입니다.");
        commentAdapter.notifyItemChanged(position);

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("content", "삭제된 댓글입니다.");

        db.collection("freeboard_posts").document(postId)
                .collection("comments").document(comment.getId())
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "댓글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteReply(Reply reply, String commentId, int position) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("content", "삭제된 답글입니다.");

        db.collection("freeboard_posts").document(postId)
                .collection("comments").document(commentId)
                .collection("replies").document(reply.getId())
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    commentAdapter.notifyItemChanged(position);
                    Toast.makeText(getContext(), "답글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "답글 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                });
    }


    private void saveReplyToFirestore(String content, String timestamp, String commentId) {
        Map<String, Object> replyData = new HashMap<>();
        replyData.put("authorId", currentUserId);
        replyData.put("content", content);
        replyData.put("timestamp", System.currentTimeMillis());

        String replyId = db.collection("freeboard_posts").document(postId)
                .collection("comments").document(commentId).collection("replies").document().getId();

        db.collection("freeboard_posts").document(postId)
                .collection("comments").document(commentId)
                .collection("replies").document(replyId)
                .set(replyData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "답글이 추가되었습니다.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "답글 추가에 실패했습니다.", Toast.LENGTH_SHORT).show());
    }

    private void onReplyClick(String commentId, CommentAdapter.CommentViewHolder holder) {
        editTextComment.setHint("답글을 입력하세요");
        buttonSubmitComment.setOnClickListener(v -> {
            String content = editTextComment.getText().toString().trim();
            if (!content.isEmpty()) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                saveReplyToFirestore(content, timestamp, commentId);
                editTextComment.setText("");
            } else {
                Toast.makeText(getContext(), "답글을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
}
