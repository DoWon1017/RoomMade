package com.example.roommade;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class CommunityBoardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private List<FreeBoardPost> postList;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private FirebaseAuth mAuth;
    private List<CheckBox> checkBoxList; // 체크박스 목록
    private boolean isDeleteMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_myfreeboard);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserId = (currentUser != null) ? currentUser.getUid() : null;

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        Button btnDelete = findViewById(R.id.btn_free_delete);
        btnDelete.setOnClickListener(v -> {
            if (isDeleteMode) {
                deleteSelectedPosts(); // 선택된 게시글 삭제
            }
            isDeleteMode = !isDeleteMode;
            postsAdapter.setDeleteMode(isDeleteMode); // 어댑터에 삭제 모드 설정
            postsAdapter.notifyDataSetChanged();
        });

        postList = new ArrayList<>();
        checkBoxList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewPosts);
        postsAdapter = new PostsAdapter(postList, null); // 체크박스 목록도 함께 전달
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postsAdapter);

        db = FirebaseFirestore.getInstance();
        fetchPosts(currentUserId); // 게시글 조회 호출
    }

    private void fetchPosts(String currentUserId) {
        if (currentUserId == null) return;

        if (registration != null) {
            registration.remove();
        }

        registration = db.collection("freeboard_posts")
                .whereEqualTo("userId", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) return;

                    postList.clear(); // clear() 호출
                    checkBoxList.clear(); // 체크박스 목록 초기화

                    for (QueryDocumentSnapshot doc : snapshot) {
                        String title = doc.getString("title");
                        String content = doc.getString("content");
                        String userId = doc.getString("userId");
                        long timestamp = doc.getLong("timestamp");
                        String postId = doc.getId();

                        FreeBoardPost post = new FreeBoardPost(title, content, userId, timestamp, postId);
                        postList.add(post);
                        CheckBox checkBox = new CheckBox(CommunityBoardActivity.this);
                        checkBox.setTag(postId); // 체크박스에 게시글 ID를 태그로 설정
                        checkBoxList.add(checkBox); // 체크박스 목록에 추가
                    }

                    postsAdapter.notifyDataSetChanged();
                });
    }

    private void deleteSelectedPosts() {
        List<String> selectedPostIds = new ArrayList<>();
        Log.d("CommunityBoard", "deleteSelectedPosts called");

        for (CheckBox checkBox : checkBoxList) {
            if (checkBox.isChecked()) {
                String postId = (String) checkBox.getTag();
                selectedPostIds.add(postId);
                Log.d("CommunityBoard", "Selected post ID: " + postId);
            }
        }

        if (!selectedPostIds.isEmpty()) {
            for (String postId : selectedPostIds) {
                db.collection("freeboard_posts").document(postId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d("CommunityBoard", "Post successfully deleted: " + postId);
                            Toast.makeText(CommunityBoardActivity.this, "게시글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                            fetchPosts(mAuth.getCurrentUser().getUid()); // 삭제 후 게시글 다시 로드
                        })
                        .addOnFailureListener(e -> {
                            Log.e("CommunityBoard", "Error deleting post ID: " + postId, e);
                            Toast.makeText(CommunityBoardActivity.this, "게시글 삭제를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Toast.makeText(this, "삭제할 게시글을 선택해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove();
        }
    }
}

