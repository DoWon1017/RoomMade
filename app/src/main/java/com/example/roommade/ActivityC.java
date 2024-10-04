package com.example.roommade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityC extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c); // activity_c.xml 레이아웃 파일을 설정

        // 배달음식 게시판 버튼 클릭 이벤트
        Button deliveryButton = findViewById(R.id.deliveryBoardButton);
        deliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityC.this, DeliveryBoardActivity.class);
                startActivity(intent); // 배달음식 게시판 액티비티로 이동
            }
        });

        // 운동 게시판 버튼 클릭 이벤트
        Button exerciseButton = findViewById(R.id.sportsBoardButton);
        exerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityC.this, ExerciseBoardActivity.class);
                startActivity(intent); // 운동 게시판 액티비티로 이동
            }
        });

        // 커뮤니티 게시판 버튼 클릭 이벤트
        Button communityButton = findViewById(R.id.communityBoardButton);
        communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityC.this, CommunityBoardActivity.class);
                startActivity(intent); // 커뮤니티 게시판 액티비티로 이동
            }
        });

        // 뒤로가기 버튼 클릭 이벤트
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티 종료
            }
        });
    }
}