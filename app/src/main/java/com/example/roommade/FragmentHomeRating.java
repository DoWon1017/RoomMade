package com.example.roommade;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FragmentHomeRating extends Fragment {

    private RatingBar ratingBar;
    private EditText ratingText;
    private Button ratingButton, cancelButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_rating, container, false);

        ratingBar = view.findViewById(R.id.ratingBar);
        ratingText = view.findViewById(R.id.ratingText);
        ratingButton = view.findViewById(R.id.rating);
        cancelButton = view.findViewById(R.id.cancel_rating);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRatingToFirestore();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });

        return view;
    }

    private void saveRatingToFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // RatingBar에서 평가 점수 가져오기
            float ratingValue = ratingBar.getRating();

            // EditText에서 코멘트 가져오기
            String comment = ratingText.getText().toString().trim();

            // 데이터 유효성 검사
            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(getContext(), "코멘트를 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firestore에 저장할 데이터 만들기
            Map<String, Object> ratingData = new HashMap<>();
            ratingData.put("rating", ratingValue);
            ratingData.put("comment", comment);

            // Firestore에 데이터 저장
            db.collection("ratings").document(userId)
                    .set(ratingData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "평가가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                navigateBack();
                            } else {
                                Toast.makeText(getContext(), "평가 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "사용자가 로그인되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }
    private void navigateBack() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }
}
