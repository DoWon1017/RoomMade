package com.example.roommade;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FragmentHomeApply extends Fragment {

    private EditText dept, grade, classNumber, name, point, distanceScore;
    private Button apply, applyCancel;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_apply, container, false);

        dept = view.findViewById(R.id.dept);
        grade = view.findViewById(R.id.grade);
        classNumber = view.findViewById(R.id.class_number);
        name = view.findViewById(R.id.name);
        point = view.findViewById(R.id.point);
        distanceScore = view.findViewById(R.id.distance_score);
        apply = view.findViewById(R.id.apply);
        applyCancel = view.findViewById(R.id.apply_cancel);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirestore();
            }
        });

        applyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });

        return view;
    }

    private void saveDataToFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            String deptText = dept.getText().toString();
            String gradeText = grade.getText().toString();
            String classNumberText = classNumber.getText().toString();
            String nameText = name.getText().toString();
            String pointText = point.getText().toString();
            String distanceScoreText = distanceScore.getText().toString();

            if (TextUtils.isEmpty(deptText) || TextUtils.isEmpty(gradeText) || TextUtils.isEmpty(classNumberText) ||
                    TextUtils.isEmpty(nameText) || TextUtils.isEmpty(pointText) || TextUtils.isEmpty(distanceScoreText)) {
                Toast.makeText(getContext(), "모든 필드를 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double pointValue = Double.parseDouble(pointText);
                double distanceScoreValue = Double.parseDouble(distanceScoreText);

                checkClassNumberAndSave(userId, deptText, gradeText, classNumberText, nameText, pointValue, distanceScoreValue);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "학점과 거리점수는 숫자로 입력해 주세요", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "사용자가 로그인되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkClassNumberAndSave(String userId, String deptText, String gradeText, String classNumberText, String nameText, double pointValue, double distanceScoreValue) {
        db.collection("apply")
                .whereEqualTo("classNumber", classNumberText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // 해당 학번의 문서가 없는 경우 저장
                                saveUserData(userId, deptText, gradeText, classNumberText, nameText, pointValue, distanceScoreValue);
                            } else {
                                // 해당 학번의 문서가 있는 경우
                                boolean userIdExists = false;
                                for (DocumentSnapshot document : task.getResult()) {
                                    if (document.getId().equals(userId)) {
                                        userIdExists = true;
                                        break;
                                    }
                                }

                                if (!userIdExists) {
                                    Toast.makeText(getContext(), "동일한 학번으로 이미 신청하셨습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // userId가 같은 경우에만 저장
                                    saveUserData(userId, deptText, gradeText, classNumberText, nameText, pointValue, distanceScoreValue);
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "데이터 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserData(String userId, String deptText, String gradeText, String classNumberText, String nameText, double pointValue, double distanceScoreValue) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("dept", deptText);
        userData.put("grade", gradeText);
        userData.put("classNumber", classNumberText);
        userData.put("name", nameText);
        userData.put("point", pointValue);
        userData.put("distanceScore", distanceScoreValue);

        db.collection("apply").document(userId)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "신청이 성공적으로 이루어졌습니다.", Toast.LENGTH_SHORT).show();
                            navigateBack();
                        } else {
                            Toast.makeText(getContext(), "신청에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateBack() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }
}
