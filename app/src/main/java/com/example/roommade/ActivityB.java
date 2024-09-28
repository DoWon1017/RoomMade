package com.example.roommade;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivityB extends AppCompatActivity {

    private EditText rewardInput, penaltyInput;
    private Button submitRewardButton, submitPenaltyButton, checkScoreButton, backButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b); // activity_b.xml 레이아웃 파일 설정

        // UI 요소 초기화
        rewardInput = findViewById(R.id.meritInput);
        penaltyInput = findViewById(R.id.demeritInput);
        submitRewardButton = findViewById(R.id.meritSubmitButton);
        submitPenaltyButton = findViewById(R.id.demeritSubmitButton);
        checkScoreButton = findViewById(R.id.viewScoreButton);
        backButton = findViewById(R.id.backButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 숫자만 입력할 수 있도록 제한
        rewardInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        penaltyInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});

        // 상점 점수 기입 버튼 클릭 리스너
        submitRewardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRewardScore();
            }
        });

        // 벌점 점수 기입 버튼 클릭 리스너
        submitPenaltyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePenaltyScore();
            }
        });

        // 상벌점 점수 조회 버튼 클릭 리스너
        checkScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTotalScore();
            }
        });

        // 뒤로가기 버튼 클릭 리스너
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티 종료
            }
        });
    }

    private void saveRewardScore() {
        String rewardText = rewardInput.getText().toString();

        if (TextUtils.isEmpty(rewardText)) {
            Toast.makeText(this, "상점 점수를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int rewardValue = Integer.parseInt(rewardText);
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                // 해당 문서가 존재하는지 확인 후 없으면 생성
                db.collection("scores").document(userId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        // 문서가 있을 때는 update
                                        db.collection("scores").document(userId)
                                                .update("score", FieldValue.increment(rewardValue))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ActivityB.this, "상점이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(ActivityB.this, "상점 저장 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // 문서가 없을 때는 새로 생성
                                        Map<String, Object> initialData = new HashMap<>();
                                        initialData.put("score", rewardValue);
                                        db.collection("scores").document(userId)
                                                .set(initialData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ActivityB.this, "상점이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(ActivityB.this, "상점 저장 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(ActivityB.this, "데이터베이스 접근 오류", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "숫자만 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePenaltyScore() {
        String penaltyText = penaltyInput.getText().toString();

        if (TextUtils.isEmpty(penaltyText)) {
            Toast.makeText(this, "벌점 점수를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int penaltyValue = Integer.parseInt(penaltyText);
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                db.collection("scores").document(userId).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        db.collection("scores").document(userId)
                                                .update("score", FieldValue.increment(-penaltyValue))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ActivityB.this, "벌점이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(ActivityB.this, "벌점 저장 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Map<String, Object> initialData = new HashMap<>();
                                        initialData.put("score", -penaltyValue);
                                        db.collection("scores").document(userId)
                                                .set(initialData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ActivityB.this, "벌점이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(ActivityB.this, "벌점 저장 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Toast.makeText(ActivityB.this, "데이터베이스 접근 오류", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "숫자만 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkTotalScore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("scores").document(userId).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    // 필드가 존재하고 값이 있을 때
                                    Long totalScore = document.getLong("score");
                                    if (totalScore != null) {
                                        // 필드에 값이 있을 경우 출력
                                        Toast.makeText(ActivityB.this, "상벌점 합산 점수는 " + totalScore + "점입니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // 필드가 null일 경우 0점으로 처리
                                        Toast.makeText(ActivityB.this, "상벌점 합산 점수는 0점입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // 문서 자체가 없을 경우
                                    Toast.makeText(ActivityB.this, "상벌점 합산 점수는 0점입니다.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ActivityB.this, "점수 조회 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
