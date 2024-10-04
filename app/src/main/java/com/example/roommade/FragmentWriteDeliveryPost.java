package com.example.roommade;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class FragmentWriteDeliveryPost extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference postsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_writedeliverypost, container, false);

        db = FirebaseFirestore.getInstance();
        postsRef = db.collection("deliveryPosts");

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentOrderDelivery fragmentOrderDelivery = new FragmentOrderDelivery();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containers, fragmentOrderDelivery)
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button btnSubmit = view.findViewById(R.id.btn_submit);
        EditText editTextTitle = view.findViewById(R.id.editTextDeliveryTitle);
        Spinner spinnerTime = view.findViewById(R.id.spinnerTime);

        String[] timeOptions = {
                "10분", "15분", "20분", "25분", "30분",
                "35분", "40분", "45분", "50분", "55분", "1시간"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, timeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String remainingTime = spinnerTime.getSelectedItem() != null ? spinnerTime.getSelectedItem().toString() : null;

                if (title.isEmpty()) {
                    Toast.makeText(getActivity(), "제목을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (remainingTime == null || remainingTime.isEmpty()) {
                    Toast.makeText(getActivity(), "시간을 선택하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                long startTime = System.currentTimeMillis();
                long timestamp = System.currentTimeMillis();

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Map<String, Object> post = new HashMap<>();
                post.put("title", title);
                post.put("remainingTime", remainingTime);
                post.put("startTime", startTime);
                post.put("timestamp", timestamp);
                post.put("userId", userId);

                postsRef.add(post)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(getActivity(), "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.containers, new FragmentOrderDelivery())
                                    .addToBackStack(null)
                                    .commit();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "게시글 작성에 실패했습니다 ", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        return view;
    }
}
