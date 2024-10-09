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

public class FragmentMyExercisePost extends Fragment {

    private RecyclerView recyclerView;
    private ExercisePostAdapter exercisePostsAdapter;
    private List<ExercisePost> exercisePostList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myexercise, container, false);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        exercisePostList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        loadExercisePosts();

        return view;
    }

    private void loadExercisePosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        db.collection("exercise_posts")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int size = task.getResult() != null ? task.getResult().size() : 0;
                        exercisePostList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String postId = document.getId();
                            String title = document.getString("title");
                            String content = document.getString("content");
                            String userId = document.getString("userId");
                            long timestamp = document.getLong("timestamp");
                            int maxParticipants = document.getLong("maxParticipants").intValue();
                            int currentParticipants = document.getLong("currentParticipants").intValue();
                            List<String> participantIds = (List<String>) document.get("participantIds");

                            ExercisePost post = new ExercisePost(
                                    postId,
                                    title,
                                    content,
                                    userId,
                                    timestamp,
                                    maxParticipants,
                                    currentParticipants,
                                    participantIds
                            );

                            exercisePostList.add(post);
                        }

                        exercisePostList.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));

                        exercisePostsAdapter = new ExercisePostAdapter(exercisePostList, this);
                        recyclerView.setAdapter(exercisePostsAdapter);
                        exercisePostsAdapter.notifyDataSetChanged();
                    }
                });
    }

}
