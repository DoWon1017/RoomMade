package com.example.roommade;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class MyFreeBoardActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private PostsAdapter adapter;
    private List<FreeBoardPost> userPosts; // 사용자 글 목록을 담을 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_myfreeboard); // fragment_myfreeboard.xml 레이아웃 파일 설정

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerViewPosts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userPosts = new ArrayList<>();

        // FragmentFreeBoard의 인스턴스를 생성하고 Adapter에 전달
        FragmentFreeBoard fragmentFreeBoard = new FragmentFreeBoard();
        adapter = new PostsAdapter(userPosts, fragmentFreeBoard); // FragmentFreeBoard 인스턴스 전달
        recyclerView.setAdapter(adapter);

        // Intent에서 게시판 타입 가져오기
        String boardType = getIntent().getStringExtra("boardType");
        if (boardType != null) {
            loadUserPosts(boardType); // 해당 게시판의 글 로드
        } else {
            Toast.makeText(this, "게시판 타입이 지정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        }

        // 뒤로가기 버튼 설정
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish()); // 현재 액티비티 종료
    }

    private void loadUserPosts(String boardType) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Firestore에서 사용자가 작성한 글만 가져옴
            db.collection("posts")
                    .whereEqualTo("userid", userId) // 사용자 ID 필터링
                    .whereEqualTo("boardType", boardType) // 게시판 타입 필터링
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                userPosts.clear(); // 기존 데이터 초기화
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    FreeBoardPost post = document.toObject(FreeBoardPost.class);
                                    userPosts.add(post); // 사용자 글 리스트에 추가
                                }
                                adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                            } else {
                                Toast.makeText(MyFreeBoardActivity.this, "게시글을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        }
    }
}