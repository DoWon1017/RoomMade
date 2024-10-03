package com.example.roommade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class ActivityC extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private PostsAdapter adapter;
    private List<FreeBoardPost> userPosts; // 사용자 글 목록을 담을 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c); // activity_c.xml 레이아웃 파일 설정

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userPosts = new ArrayList<>();

        // FragmentFreeBoard 대신 null 전달
        adapter = new PostsAdapter(userPosts, null);
        recyclerView.setAdapter(adapter);

        // 뒤로가기 버튼 설정
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish()); // 현재 액티비티 종료


        // 각 게시판 조회하기 버튼 클릭 리스너 설정
        Button deliveryBoardButton = findViewById(R.id.deliveryBoardButton);
        deliveryBoardButton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityC.this, MyFreeBoardActivity.class);
            intent.putExtra("boardType", "deliveryposts"); // 게시판 타입 전달
            startActivity(intent);
        });

        Button sportsBoardButton = findViewById(R.id.sportsBoardButton);
        sportsBoardButton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityC.this, MyFreeBoardActivity.class);
            intent.putExtra("boardType", "exercise_posts"); // 게시판 타입 전달
            startActivity(intent);
        });

        Button communityBoardButton = findViewById(R.id.communityBoardButton);
        communityBoardButton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityC.this, MyFreeBoardActivity.class);
            intent.putExtra("boardType", "freeboard_posts"); // 게시판 타입 전달
            startActivity(intent);
        });
    }

    private void loadUserPosts(String boardType) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // 게시판 종류에 따라 Firestore에서 데이터 가져오기
            String collectionName = "";
            switch (boardType) {
                case "delivery":
                    collectionName = "deliveryPosts";
                    break;
                case "exercise":
                    collectionName = "exercise_posts";
                    break;
                case "free":
                    collectionName = "freeboard_post";
                    break;
                default:
                    break;
            }

            db.collection(collectionName)
                    .whereEqualTo("userId", userId) // userId로 필터링
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                userPosts.clear(); // 기존 데이터 초기화
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    FreeBoardPost post = document.toObject(FreeBoardPost.class); // 데이터 모델에 맞춰 변환
                                    userPosts.add(post); // 사용자 글 리스트에 추가
                                }
                                // 데이터가 로드된 후의 로그
                                Log.d("ActivityC", "Loaded user posts: " + userPosts.size());
                                adapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                            } else {
                                Toast.makeText(ActivityC.this, "게시글을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(ActivityC.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
        }
    }
}