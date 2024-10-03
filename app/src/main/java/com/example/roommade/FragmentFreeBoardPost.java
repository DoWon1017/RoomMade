package com.example.roommade;

import androidx.fragment.app.Fragment;
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
    private int anonymousCounter = 1;
    private Map<String, Integer> userAnonymousMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freeboardpost, container, false);

        db = FirebaseFirestore.getInstance();

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

            TextView textViewTitle = view.findViewById(R.id.textViewTitle);
            TextView textViewContent = view.findViewById(R.id.textViewContent);
            TextView textViewAuthor = view.findViewById(R.id.textViewAuthor);
            TextView textViewTimestamp = view.findViewById(R.id.textViewTimestamp);

            textViewTitle.setText(post.getTitle());
            textViewContent.setText(post.getContent());
            textViewAuthor.setText("익명");
            textViewTimestamp.setText(formatDate(post.getTimestamp()));
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
        commentAdapter = new CommentAdapter(commentList, (commentId, author, content) -> saveReplyToFirestore(commentId, content));

        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewComments.setAdapter(commentAdapter);

        loadComments();

        buttonSubmitComment.setOnClickListener(v -> {
            String content = editTextComment.getText().toString().trim();
            if (!content.isEmpty()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String author = getAuthor(user);

                String timestamp = String.valueOf(System.currentTimeMillis());
                String commentId = db.collection("freeboard_posts").document(postId).collection("comments").document().getId();
                saveCommentToFirestore(commentId, author, content, timestamp);
            } else {
                Toast.makeText(getContext(), "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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
                            String author = doc.getString("author");
                            String content = doc.getString("content");
                            long timestamp = doc.getLong("timestamp");

                            Comment comment = new Comment(id, author, content, timestamp);
                            commentList.add(comment);
                            loadRepliesForComment(comment);
                        }
                    }
                    commentAdapter.notifyDataSetChanged();
                });
    }


    private void loadRepliesForComment(Comment comment) {
        db.collection("freeboard_posts").document(postId).collection("comments")
                .document(comment.getId()).collection("replies")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "대댓글을 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        comment.getReplies().clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String replyId = doc.getId();
                            String replyAuthor = doc.getString("author");
                            String replyContent = doc.getString("content");
                            long replyTimestamp = doc.getLong("timestamp");

                            Comment reply = new Comment(replyId, replyAuthor, replyContent, replyTimestamp);
                            comment.addReply(reply);
                        }
                    }
                    commentAdapter.notifyDataSetChanged();
                });
    }


    private void saveCommentToFirestore(String commentId, String author, String content, String timestamp) {
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("author", author);
        commentData.put("content", content);
        commentData.put("timestamp", Long.parseLong(timestamp));

        db.collection("freeboard_posts").document(postId).collection("comments").document(commentId)
                .set(commentData)
                .addOnSuccessListener(aVoid -> {
                    editTextComment.setText("");
                    Toast.makeText(getContext(), "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    loadComments();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "댓글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show());
    }

    private void saveReplyToFirestore(String commentId, String content) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String author = getAuthor(user);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String replyId = db.collection("freeboard_posts").document(postId).collection("comments").document(commentId).collection("replies").document().getId();

        Map<String, Object> replyData = new HashMap<>();
        replyData.put("author", author);
        replyData.put("content", content);
        replyData.put("timestamp", Long.parseLong(timestamp));

        db.collection("freeboard_posts").document(postId).collection("comments").document(commentId).collection("replies").document(replyId)
                .set(replyData)
                .addOnSuccessListener(aVoid -> {
                    loadComments();
                    Toast.makeText(getContext(), "대댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "대댓글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show());
    }

    private String formatDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }

    private String getAuthor(FirebaseUser user) {
        if (user != null) {
            String userId = user.getUid();
            if (userId.equals(postUserId)) {
                return "작성자";
            } else {
                return "익명" + userAnonymousMap.computeIfAbsent(userId, k -> anonymousCounter++);
            }
        }
        return "익명" + anonymousCounter++;
    }
}
