package com.example.roommade;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_myfreeboard);

        // Firebase Auth 인스턴스 가져오기
        mAuth = FirebaseAuth.getInstance();

        // 현재 로그인된 사용자 정보 가져오기
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserId = (currentUser != null) ? currentUser.getUid() : null;

        // 뒤로가기 버튼 설정
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 이전 페이지로 돌아가기
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(postList, null); // PostsAdapter에 Fragment를 넘기지 않음
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postsAdapter);

        db = FirebaseFirestore.getInstance();
        fetchPosts(currentUserId); // Firestore에서 게시글을 가져옴
    }

    private void fetchPosts(String currentUserId) {
        if (currentUserId == null) {
            // 로그인된 사용자가 없을 경우 에러 처리
            return;
        }

        // Firestore에서 현재 사용자의 게시글만 가져오기
        registration = db.collection("freeboard_posts")
                .whereEqualTo("userId", currentUserId) // Firestore의 userId 필드와 현재 로그인된 사용자 ID가 일치하는 게시글만 가져옴
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshot, FirebaseFirestoreException e) {
                        if (e != null) {
                            // 에러 처리
                            return;
                        }
                        postList.clear(); // 기존 리스트 비우기
                        for (QueryDocumentSnapshot doc : snapshot) {
                            FreeBoardPost post = doc.toObject(FreeBoardPost.class);
                            postList.add(post); // 새 게시글 추가
                        }
                        postsAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알리기
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registration != null) {
            registration.remove(); // 리스너 해제
        }
    }
}
