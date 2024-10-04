package com.example.roommade;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ActivityA extends AppCompatActivity {

    private EditText name, grade, birthDate, location;
    private CheckBox monWedMorning, tueThuMorning;  // 체크박스 추가
    private Button apply, applyCancel;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a); // activity_a.xml 레이아웃 파일을 설정

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // EditText 초기화
        name = findViewById(R.id.nameInput);
        grade = findViewById(R.id.gradeInput);
        birthDate = findViewById(R.id.birthDateInput); // 생년월일 입력 EditText
        location = findViewById(R.id.residenceInput); // 사는 지역 입력 EditText

        // 체크박스 초기화
        monWedMorning = findViewById(R.id.monWedMorning);
        tueThuMorning = findViewById(R.id.tueThuMorning);

        // 버튼 초기화
        apply = findViewById(R.id.submitButton);

        // 기입 버튼 클릭 리스너
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirestore();
            }
        });

        // 뒤로가기 버튼 설정
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티 종료
            }
        });
    }

    private void saveDataToFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            String nameText = name.getText().toString();
            String gradeText = grade.getText().toString();
            String birthDateText = birthDate.getText().toString();
            String locationText = location.getText().toString();

            if (TextUtils.isEmpty(nameText) || TextUtils.isEmpty(gradeText) ||
                    TextUtils.isEmpty(birthDateText) || TextUtils.isEmpty(locationText)) {
                Toast.makeText(getApplicationContext(), "모든 필드를 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 체크박스 선택 여부에 따라 저장할 값 설정
            int schedule = 0;
            if (monWedMorning.isChecked() && tueThuMorning.isChecked()) {
                schedule = 3;  // 둘 다 체크된 경우
            } else if (monWedMorning.isChecked()) {
                schedule = 1;  // 월/수 오전만 체크된 경우
            } else if (tueThuMorning.isChecked()) {
                schedule = 2;  // 화/목 오전만 체크된 경우
            }

            // 데이터베이스에 저장
            saveUserData(userId, nameText, gradeText, birthDateText, locationText);

            //시간표 정보만 따로 passed_users에 업데이트
            saveUserData(userId, schedule);
        } else {
            Toast.makeText(getApplicationContext(), "사용자가 로그인되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserData(String userId, String nameText, String gradeText, String birthDateText, String locationText) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", nameText);
        userData.put("grade", gradeText);
        userData.put("birthDate", birthDateText);
        userData.put("location", locationText);


        db.collection("information").document(userId)
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "정보기입이 성공적으로 이루어졌습니다.", Toast.LENGTH_SHORT).show();
                            navigateBack();
                        } else {
                            Toast.makeText(getApplicationContext(), "정보기입이 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //시간표 정보만 따로 passed_users에 업데이트
    private void saveUserData(String userId, int schedule) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("schedule", schedule);

        // Use update instead of set to only modify the schedule field
        db.collection("passed_users").document(userId)
                .update(userData)  // Update the specific field
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "정보기입이 성공적으로 이루어졌습니다.", Toast.LENGTH_SHORT).show();
                            navigateBack();
                        } else {
                            Toast.makeText(getApplicationContext(), "시간표기입은 합격자만 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void navigateBack() {
        finish(); // 현재 액티비티 종료
    }
}
