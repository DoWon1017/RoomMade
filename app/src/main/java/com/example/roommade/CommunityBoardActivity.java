package com.example.roommade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_myfreeboard); // fragment_myfreeboard.xml 파일 설정

        // 뒤로가기 버튼 설정
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 바로 이전 페이지로 이동
                Intent intent = new Intent(CommunityBoardActivity.this, ActivityC.class); // ActivityC는 이전 페이지의 액티비티 클래스
                startActivity(intent);
                // 현재 액티비티 종료
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(postList, null); // 여기서 fragmentFreeBoard 대신 null 사용
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postsAdapter);

        db = FirebaseFirestore.getInstance();
        fetchPosts(); // Firestore에서 게시글을 가져옴
    }

    private void fetchPosts() {
        registration = db.collection("community_posts")
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
