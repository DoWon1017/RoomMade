package com.example.roommade;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // 레이아웃 찾기
        LinearLayout checkLayout = view.findViewById(R.id.check_layout);
        LinearLayout applyLayout = view.findViewById(R.id.apply_layout);
        LinearLayout ratingLayout = view.findViewById(R.id.rating_layout);
        LinearLayout notificationLayout = view.findViewById(R.id.notification_layout);

        // 클릭 리스너 설정
        checkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // 현재 사용자의 점수 및 거리 점수 확인
                    getUserScoresAndCheckIfTop5(user.getUid());
                } else {
                    Toast.makeText(getContext(), "사용자가 로그인되어 있지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        applyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(new FragmentHomeApply());
            }
        });

        ratingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(new FragmentHomeRating());
            }
        });

        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(new FragmentHomeNotification());
            }
        });

        return view;
    }

    private void getUserScoresAndCheckIfTop5(String userId) {
        db.collection("apply").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // 사용자 점수 및 거리 점수 가져오기
                                double point = document.getDouble("point");
                                double distanceScore = document.getDouble("distanceScore");

                                // 상위 5명에 들 수 있는지 확인
                                checkIfTop5(point, distanceScore);
                            } else {
                                Toast.makeText(getContext(), "사용자 데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "데이터 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkIfTop5(double currentPoint, double currentDistanceScore) {
        // 현재 사용자의 합계 점수
        double currentTotalScore = currentPoint + currentDistanceScore;

        // Firestore에서 상위 5명의 데이터를 가져오기 (합계 점수 기준 내림차순)
        db.collection("apply")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Double> totalScores = new ArrayList<>();

                            for (DocumentSnapshot document : task.getResult()) {
                                // point 및 distanceScore 필드의 값을 가져오기
                                Double point = document.getDouble("point");
                                Double distanceScore = document.getDouble("distanceScore");

                                if (point != null && distanceScore != null) {
                                    double totalScore = point + distanceScore;
                                    totalScores.add(totalScore);
                                } else {
                                    // 필드가 null인 경우 로그 출력
                                    Toast.makeText(getContext(), "point 또는 distanceScore 필드가 null입니다.", Toast.LENGTH_LONG).show();
                                }
                            }

                            // 점수 내림차순으로 정렬
                            totalScores.sort((score1, score2) -> Double.compare(score2, score1));

                            if (totalScores.size() >= 5 && currentTotalScore >= totalScores.get(4)) {
                                // 사용자의 합계 점수가 상위 5명에 포함됨
                                showResultDialog("축하합니다! 합격하셨습니다.\n 마이페이지에서 시간표정보를 기입해 주세요");
                            } else if (totalScores.size() < 5 && currentTotalScore >= totalScores.get(totalScores.size() - 1)) {
                                // 데이터의 수가 다 채워지지 않았을때
                                showResultDialog("인원미달");
                            } else {
                                // 사용자가 상위 5명 안에 들지 못함
                                showResultDialog("아쉽습니다. 불합격입니다.");
                            }
                        } else {
                            // 쿼리 실패 시
                            Toast.makeText(getContext(), "확인 중 오류가 발생했습니다: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showResultDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("결과")
                .setMessage(message)
                .setPositiveButton("확인", null)
                .show();
    }

    private void navigateToFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.containers, fragment)
                .addToBackStack(null)
                .commit();
    }


}
