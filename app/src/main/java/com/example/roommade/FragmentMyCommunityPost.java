package com.example.roommade;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FragmentMyCommunityPost extends Fragment {

    private RecyclerView recyclerView;
    private List<FreeBoardPost> postList;
    private Button btnDelete;
    private boolean isDeleteMode = false;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myfreeboard, container, false);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        btnDelete = view.findViewById(R.id.btn_free_delete);

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        postList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        loadPosts();

        btnDelete.setOnClickListener(v -> {
            if (!isDeleteMode) {
                isDeleteMode = true;
                btnDelete.setText("삭제하기");
                ((PostsAdapter) recyclerView.getAdapter()).setDeleteMode(true);
            } else {
                List<FreeBoardPost> selectedPosts = ((PostsAdapter) recyclerView.getAdapter()).getSelectedPosts();
                if (selectedPosts.isEmpty()) {
                    Toast.makeText(getActivity(), "게시글을 삭제하려면 체크박스를 체크하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    deleteSelectedPosts(selectedPosts);
                }
            }
        });

        return view;
    }

    private void loadPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        db.collection("freeboard_posts")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String postId = document.getId();
                            String title = document.getString("title");
                            String content = document.getString("content");
                            String userId = document.getString("userId");
                            long timestamp = document.getLong("timestamp");

                            FreeBoardPost post = new FreeBoardPost(title, content, userId, timestamp, postId);
                            postList.add(post);
                        }

                        postList.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));
                        recyclerView.setAdapter(new PostsAdapter(postList, this));
                    }
                });
    }

    private void deleteSelectedPosts(List<FreeBoardPost> selectedPosts) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        for (FreeBoardPost post : selectedPosts) {
            if (post.getUserId().equals(currentUserId)) {
                db.collection("freeboard_posts").document(post.getPostId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getActivity(), "게시글 삭제 완료", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "게시글 삭제 실패", Toast.LENGTH_SHORT).show();
                        });
            }
        }

        isDeleteMode = false;
        btnDelete.setText("삭제");
        loadPosts();
    }
}
