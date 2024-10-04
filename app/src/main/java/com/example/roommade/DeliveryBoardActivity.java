package com.example.roommade;

import android.os.Bundle;
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

public class DeliveryBoardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private List<FreeBoardPost> postList;
    private FirebaseFirestore db;
    private ListenerRegistration registration;
    private FirebaseAuth mAuth;
    private LinearLayout checkboxLayout; // 체크박스 레이아웃 추가
    private List<CheckBox> checkBoxList; // 체크박스 목록

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mydelivery);

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

        // 삭제하기 버튼 설정
        Button btnDelete = findViewById(R.id.btn_delivery_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 체크박스 레이아웃의 가시성 전환
                if (checkboxLayout.getVisibility() == View.GONE) {
                    checkboxLayout.setVisibility(View.VISIBLE); // 체크박스 보이기
                } else {
                    deleteSelectedPosts(); // 선택된 게시글 삭제
                }
            }
        });

        checkboxLayout = findViewById(R.id.checkboxLayout); // 체크박스 레이아웃 참조
        checkboxLayout.setVisibility(View.GONE); // 체크박스 레이아웃 초기 숨김

        postList = new ArrayList<>();
        checkBoxList = new ArrayList<>(); // 체크박스 목록 초기화

        recyclerView = findViewById(R.id.recyclerViewPosts); // 올바른 RecyclerView ID 참조
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
        registration = db.collection("deliveryPosts")
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
                        checkBoxList.clear(); // 체크박스 목록 초기화
                        checkboxLayout.removeAllViews(); // 체크박스 레이아웃 초기화

                        for (QueryDocumentSnapshot doc : snapshot) {
                            FreeBoardPost post = doc.toObject(FreeBoardPost.class);
                            postList.add(post); // 새 게시글 추가

                            // 체크박스 추가
                            CheckBox checkBox = new CheckBox(DeliveryBoardActivity.this);
                            checkBox.setTag(doc.getId()); // 체크박스에 게시글 ID를 태그로 설정
                            checkboxLayout.addView(checkBox); // 체크박스 레이아웃에 추가
                            checkBoxList.add(checkBox); // 체크박스 목록에 추가
                        }

                        postsAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알리기
                    }
                });
    }

    private void deleteSelectedPosts() {
        List<String> selectedPostIds = new ArrayList<>(); // 선택된 게시글 ID 목록

        // 체크박스 목록을 돌며 선택된 게시글 ID를 수집
        for (CheckBox checkBox : checkBoxList) {
            if (checkBox.isChecked()) {
                selectedPostIds.add((String) checkBox.getTag());
            }
        }

        // 선택된 게시글이 있을 경우 삭제 처리
        if (!selectedPostIds.isEmpty()) {
            for (String postId : selectedPostIds) {
                db.collection("deliveryPosts").document(postId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(DeliveryBoardActivity.this, "게시글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(DeliveryBoardActivity.this, "게시글 삭제를 실패하였습니다.", Toast.LENGTH_SHORT).show();
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
            registration.remove(); // 리스너 해제
        }
    }
}
