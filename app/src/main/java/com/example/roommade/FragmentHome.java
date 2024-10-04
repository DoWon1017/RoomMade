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
import java.util.*;

public class FragmentHome extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // 방설정
    private static final int CAPACITY_OF_ROOM_TWO= 2; // 2인실
    private static final int CAPACITY_OF_ROOM_THREE = 3; // 3인실
    private static final int NUMBER_OF_ROOM_TWO = 2; // 2인실의 갯수
    private static final int NUMBER_OF_ROOM_THREE = 1; // 3인실의 갯수

    //합격자 수 = 2x2인실의 갯수 + 3x3인실의 갯수
    private static final int passNum = CAPACITY_OF_ROOM_TWO*NUMBER_OF_ROOM_TWO+CAPACITY_OF_ROOM_THREE*NUMBER_OF_ROOM_THREE;


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
        LinearLayout roommateLayout = view.findViewById(R.id.roommate_layout);

        // 클릭 리스너 설정
        checkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // 현재 사용자의 점수 및 거리 점수 확인
                    getUserScoresAndCheckIfPass(user.getUid());
                } else {
                    Toast.makeText(getContext(), "사용자가 로그인되어 있지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        roommateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {

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

    private void getUserScoresAndCheckIfPass(String userId) {
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

                                // 합격선에 들 수 있는지 확인
                                checkIfPass(point, distanceScore);
                            } else {
                                Toast.makeText(getContext(), "기숙사 신청을 해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "데이터 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkIfPass(double currentPoint, double currentDistanceScore) {
        // 현재 사용자의 합계 점수
        double currentTotalScore = currentPoint + currentDistanceScore;

        // Firestore에서 신청자들의 데이터를 가져오기 (합계 점수 기준 내림차순)
        db.collection("apply")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<UserScore> allUserScores = new ArrayList<>();
                            List<UserScore> passedUserScores = new ArrayList<>();

                            for (DocumentSnapshot document : task.getResult()) {
                                // point 및 distanceScore 필드의 값을 가져오기
                                Double point = document.getDouble("point");
                                Double distanceScore = document.getDouble("distanceScore");
                                String userId = document.getId(); // 유저 ID 가져오기
                                String name = document.getString("name");

                                if (point != null && distanceScore != null) {
                                    double totalScore = point + distanceScore;
                                    allUserScores.add(new UserScore(userId, name, point, distanceScore, totalScore));
                                } else {
                                    // 필드가 null인 경우 로그 출력
                                    Toast.makeText(getContext(), "point 또는 distanceScore 필드가 null입니다.", Toast.LENGTH_LONG).show();
                                }
                            }

                            // 점수 내림차순으로 정렬
                            allUserScores.sort((user1, user2) -> Double.compare(user2.totalScore, user1.totalScore));

                            // 합격자 명단 추리기
                            for (int i = 0; i < passNum && i < allUserScores.size(); i++) {
                                passedUserScores.add(allUserScores.get(i));
                            }

                            // 현재 사용자가 합격했는지 여부 확인
                            boolean isCurrentUserPassed = currentTotalScore >= passedUserScores.get(passedUserScores.size() - 1).totalScore;

                            if (isCurrentUserPassed) {
                                // 사용자의 합계 점수가 합격선에 포함됨
                                showResultDialog("축하합니다! 합격하셨습니다.\n 마이페이지에서 시간표정보를 기입해 주세요");
                            } else {
                                // 사용자가 합격선 안에 들지 못함
                                showResultDialog("아쉽습니다. 불합격입니다.");
                            }

                            // 합격자 명단 Firestore에 저장
                            savePassedUsers(passedUserScores);

                        } else {
                            // 쿼리 실패 시
                            Toast.makeText(getContext(), "확인 중 오류가 발생했습니다: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // 합격자 명단을 Firestore에 저장하는 메서드
    private void savePassedUsers(List<UserScore> passedUserScores) {
        // 먼저 기존의 passed_users 컬렉션을 비운다
        db.collection("passed_users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // 기존 문서들 삭제
                            for (DocumentSnapshot document : task.getResult()) {
                                db.collection("passed_users").document(document.getId()).delete();
                            }
                            // 기존 문서 삭제 후 새로운 합격자 명단 저장
                            storeNewPassedUsers(passedUserScores);
                        } else {
                            Toast.makeText(getContext(), "기존 명단 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 새로운 합격자 명단을 저장하는 메서드
    private void storeNewPassedUsers(List<UserScore> passedUserScores) {
        for (UserScore userScore : passedUserScores) {
            db.collection("passed_users").document(userScore.userId)
                    .set(userScore)
                    .addOnSuccessListener(aVoid -> {

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "합격자 명단 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    });
        }
    }




    public class UserScore {
        String userId;
        String name;
        double point;
        double distanceScore;
        double totalScore;
        int schedule;

        public UserScore() {
            // Firestore에서 필요로 하는 기본 생성자
        }

        public UserScore(String userId, String name, double point, double distanceScore, double totalScore) {
            this.userId = userId;
            this.name = name;
            this.point = point;
            this.distanceScore = distanceScore;
            this.totalScore = totalScore;
        }

        public UserScore(String userId, String name, double point, double distanceScore, double totalScore, int schedule) {
            this.userId = userId;
            this.name = name;
            this.point = point;
            this.distanceScore = distanceScore;
            this.totalScore = totalScore;
            this.schedule = schedule; // 추가: 사용자 시간표
        }

        // getter 및 setter 추가
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public double getPoint() {
            return point;
        }

        public void setPoint(double point) {
            this.point = point;
        }

        public double getDistanceScore() {
            return distanceScore;
        }

        public void setDistanceScore(double distanceScore) {
            this.distanceScore = distanceScore;
        }

        public double getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(double totalScore) {
            this.totalScore = totalScore;
        }
        public int getSchedule() {
            return schedule;
        }

        public void setSchedule(int schedule) {
            this.schedule = schedule;
        }
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
