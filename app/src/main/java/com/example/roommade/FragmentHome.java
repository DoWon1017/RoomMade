package com.example.roommade;

import com.example.roommade.UserScore;
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
                    // Fetch the passed users and assign rooms
                    assignRoomsBasedOnSchedule();
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
                                int schedule = 0;

                                if (point != null && distanceScore != null) {
                                    double totalScore = point + distanceScore;
                                    allUserScores.add(new UserScore(userId, name, point, distanceScore, totalScore, schedule));
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

    private void assignRoomsBasedOnSchedule() {
        // Firestore에서 합격한 사용자 가져오기
        db.collection("passed_users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<UserScore> passedUsers = new ArrayList<>();

                    for (DocumentSnapshot document : task.getResult()) {
                        UserScore user = document.toObject(UserScore.class);
                        if (user != null) {
                            passedUsers.add(user);
                        } else {
                            Toast.makeText(getContext(), "사용자 데이터를 변환하는 데 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    // 총 점수에 따라 합격자를 정렬 (높은 점수 순)
                    passedUsers.sort((u1, u2) -> Double.compare(u2.getTotalScore(), u1.getTotalScore()));

                    // 스케줄별로 사용자를 그룹화할 맵 생성
                    Map<Integer, List<UserScore>> usersBySchedule = new HashMap<>();
                    for (int i = 0; i < 4; i++) {
                        usersBySchedule.put(i, new ArrayList<>());
                    }

                    for (UserScore user : passedUsers) {
                        int schedule = user.getSchedule();
                        usersBySchedule.get(schedule).add(user);
                    }

                    // 이제 방 배정 시작
                    List<List<UserScore>> rooms = new ArrayList<>();
                    assignRoomsToSchedules(usersBySchedule, rooms);

                    // 남은 사용자가 균등하게 맞지 않을 경우 처리
                    assignRemainingUsers(usersBySchedule, rooms);

                    // 방 배정을 Firestore에 저장
                    saveRoomAssignments(rooms);

                    // 방 배정 결과 알림창 표시
                    showRoomAssignments(rooms);

                } else {
                    Toast.makeText(getContext(), "합격자 데이터를 가져오는 중 오류가 발생했습니다." + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void assignRoomsToSchedules(Map<Integer, List<UserScore>> usersBySchedule, List<List<UserScore>> rooms) {
        // 같은 스케줄 내에서 방을 먼저 배정하고, 남은 용량에 따라 배정
        int twoPersonRoomsLeft = NUMBER_OF_ROOM_TWO;
        int threePersonRoomsLeft = NUMBER_OF_ROOM_THREE;

        for (int schedule = 0; schedule < 4; schedule++) {
            List<UserScore> users = usersBySchedule.get(schedule);

            while (!users.isEmpty()) {
                if (twoPersonRoomsLeft > 0 && users.size() >= 2) {
                    // 2인실 배정
                    rooms.add(new ArrayList<>(users.subList(0, 2)));
                    users.subList(0, 2).clear();
                    twoPersonRoomsLeft--;
                } else if (threePersonRoomsLeft > 0 && users.size() >= 3) {
                    // 3인실 배정
                    rooms.add(new ArrayList<>(users.subList(0, 3)));
                    users.subList(0, 3).clear();
                    threePersonRoomsLeft--;
                } else {
                    break; // 남은 방을 배정할 수 없음
                }
            }
        }
    }

    private void assignRemainingUsers(Map<Integer, List<UserScore>> usersBySchedule, List<List<UserScore>> rooms) {
        // 이제 남은 사용자들을 배정, 스케줄이 달라도 상관 없음
        int twoPersonRoomsLeft = NUMBER_OF_ROOM_TWO - rooms.size(); // 남은 방 수 확인
        int threePersonRoomsLeft = NUMBER_OF_ROOM_THREE - rooms.size();

        List<UserScore> remainingUsers = new ArrayList<>();
        for (List<UserScore> users : usersBySchedule.values()) {
            remainingUsers.addAll(users);
        }

        while (!remainingUsers.isEmpty()) {
            if (twoPersonRoomsLeft > 0 && remainingUsers.size() >= 2) {
                // 남은 2인실 배정
                rooms.add(new ArrayList<>(remainingUsers.subList(0, 2)));
                remainingUsers.subList(0, 2).clear();
                twoPersonRoomsLeft--;
            } else if (threePersonRoomsLeft > 0 && remainingUsers.size() >= 3) {
                // 남은 3인실 배정
                rooms.add(new ArrayList<>(remainingUsers.subList(0, 3)));
                remainingUsers.subList(0, 3).clear();
                threePersonRoomsLeft--;
            } else {
                break; // 더 이상 배정할 방 없음
            }
        }
    }

    private void saveRoomAssignments(List<List<UserScore>> rooms) {
        // 기존 방 배정 삭제
        db.collection("room_assignments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        db.collection("room_assignments").document(document.getId()).delete();
                    }

                    // 새로운 방 배정 저장
                    for (int i = 0; i < rooms.size(); i++) {
                        List<UserScore> room = rooms.get(i);
                        Map<String, Object> roomData = new HashMap<>();
                        roomData.put("roomNumber", i + 1);

                        List<String> userIds = new ArrayList<>();
                        for (UserScore user : room) {
                            userIds.add(user.getName());
                        }
                        roomData.put("userIds", userIds);

                        db.collection("room_assignments").add(roomData)
                                .addOnSuccessListener(aVoid -> {

                                    // 성공 처리
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "방 배정 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(getContext(), "기존 배정 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
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

    private void showRoomAssignments(List<List<UserScore>> rooms) {
        StringBuilder message = new StringBuilder("방 배정 결과:\n");
        for (int i = 0; i < rooms.size(); i++) {
            message.append("방 번호 ").append(i + 1).append(": ");
            for (UserScore user : rooms.get(i)) {
                message.append(user.getName()).append(", ");
            }
            // 마지막 쉼표 제거
            message.setLength(message.length() - 2);
            message.append("\n");
        }

        // 결과 알림창 띄우기
        new AlertDialog.Builder(getContext())
                .setTitle("방 배정 결과")
                .setMessage(message.toString())
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
