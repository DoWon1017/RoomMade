package com.example.roommade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class FragmentExercise extends Fragment {

    private RecyclerView recyclerViewPosts;
    private ExercisePostAdapter postsAdapter;
    private List<ExercisePost> exercisePostList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        exercisePostList = new ArrayList<>();
        postsAdapter = new ExercisePostAdapter(exercisePostList, this);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewPosts.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerViewPosts.addItemDecoration(dividerItemDecoration);

        recyclerViewPosts.setAdapter(postsAdapter);

        loadPosts();

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCommunity fragmentCommunity = new FragmentCommunity();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containers, fragmentCommunity)
                        .addToBackStack(null)
                        .commit();
            }
        });

        Button btnWritePost = view.findViewById(R.id.btn_write_post);
        btnWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentWriteExercisePost fragmentWriteExercisePost = new FragmentWriteExercisePost();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containers, fragmentWriteExercisePost)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void loadPosts() {
        db.collection("exercise_posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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
                        postsAdapter.notifyDataSetChanged();
                    }
                });
    }

}
