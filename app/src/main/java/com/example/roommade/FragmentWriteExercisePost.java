package com.example.roommade;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FragmentWriteExercisePost extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_writeexercisepost, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentExercise fragmentExercise = new FragmentExercise();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containers, fragmentExercise)
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button btnWritePost = view.findViewById(R.id.btn_write_post);
        EditText editTextTitle = view.findViewById(R.id.editTextExerciseTitle);
        EditText editTextContent = view.findViewById(R.id.editTextExerciseContent);

        Spinner spinnerParticipants = view.findViewById(R.id.spinnerExerciseParticipants);
        String[] participantsOptions = {"2명", "3명", "4명", "5명", "6명", "7명", "8명", "9명", "10명", "11명", "12명", "13명", "14명", "15명", "16명", "17명", "18명", "19명", "20명"};
        ArrayAdapter<String> participantsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, participantsOptions);
        participantsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParticipants.setAdapter(participantsAdapter);

        btnWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String content = editTextContent.getText().toString();
                String selectedParticipants = spinnerParticipants.getSelectedItem() != null ? spinnerParticipants.getSelectedItem().toString() : null;

                if (title.isEmpty() || content.isEmpty() || selectedParticipants == null) {
                    Toast.makeText(getActivity(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    int maxParticipants = Integer.parseInt(selectedParticipants.replace("명", ""));
                    saveExercisePostToFirestore(title, content, maxParticipants);
                }
            }
        });

        return view;
    }

    private void saveExercisePostToFirestore(String title, String content, int maxParticipants) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            Map<String, Object> post = new HashMap<>();
            post.put("title", title);
            post.put("content", content);
            post.put("userId", userId);
            post.put("timestamp", System.currentTimeMillis());
            post.put("maxParticipants", maxParticipants);
            post.put("currentParticipants", 1);  // 초기 참여 인원 설정

            db.collection("exercise_posts")
                    .document()
                    .set(post)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                                clearFields();

                                FragmentExercisePost fragmentExercisePost = new FragmentExercisePost();
                                Bundle args = new Bundle();
                                args.putString("title", title);
                                args.putString("content", content);
                                args.putString("userId", userId);
                                args.putLong("timestamp", System.currentTimeMillis());
                                args.putInt("maxParticipants", maxParticipants);
                                args.putInt("currentParticipants", 1); // 여기에서 현재 참여 인원을 전달
                                fragmentExercisePost.setArguments(args);

                                getParentFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.containers, fragmentExercisePost)
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                Toast.makeText(getActivity(), "게시글 작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "사용자가 로그인되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        EditText editTextTitle = getView().findViewById(R.id.editTextExerciseTitle);
        EditText editTextContent = getView().findViewById(R.id.editTextExerciseContent);
        editTextTitle.setText("");
        editTextContent.setText("");
    }
}
