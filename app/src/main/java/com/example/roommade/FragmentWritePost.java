package com.example.roommade;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;


import java.util.HashMap;
import java.util.Map;

public class FragmentWritePost extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_writepost, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        Button btnWritePost = view.findViewById(R.id.btn_write_post);
        EditText editTextTitle = view.findViewById(R.id.editTextTitle);
        EditText editTextContent = view.findViewById(R.id.editTextContent);

        btnWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String content = editTextContent.getText().toString();

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(getActivity(), "제목과 내용을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    savePostToFirestore(title, content);
                }
            }
        });

        return view;
    }

    private void savePostToFirestore(String title, String content) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Firestore에 새 문서 생성
            Map<String, Object> post = new HashMap<>();
            post.put("title", title);
            post.put("content", content);
            post.put("userId", userId);
            post.put("timestamp", System.currentTimeMillis());

            db.collection("freeboard_posts")
                    .add(post)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                String postId = task.getResult().getId();

                                Toast.makeText(getActivity(), "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show();
                                clearFields();

                                FragmentFreeBoardPost fragmentFreeBoardPost = new FragmentFreeBoardPost();
                                Bundle args = new Bundle();
                                args.putString("postId", postId);
                                args.putString("title", title);
                                args.putString("content", content);
                                args.putString("userId", userId);
                                args.putLong("timestamp", System.currentTimeMillis());
                                fragmentFreeBoardPost.setArguments(args);

                                getParentFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.containers, fragmentFreeBoardPost)
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
        EditText editTextTitle = getView().findViewById(R.id.editTextTitle);
        EditText editTextContent = getView().findViewById(R.id.editTextContent);
        editTextTitle.setText("");
        editTextContent.setText("");
    }
}



