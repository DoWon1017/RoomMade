package com.example.roommade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private PostsAdapter postsAdapter;
    private List<FreeBoardPost> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myfreeboard, container, false);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        postList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        loadPosts();

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

                        postsAdapter = new PostsAdapter(postList, this);
                        recyclerView.setAdapter(postsAdapter);
                        postsAdapter.notifyDataSetChanged();
                    }
                });
    }
}
