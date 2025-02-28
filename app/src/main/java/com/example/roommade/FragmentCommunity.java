package com.example.roommade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentCommunity extends Fragment {

    private Button btnFreeBoard;
    private Button btnOrderDelivery;
    private Button btnExercise;
    private RecyclerView recyclerViewFreeBoard;
    private RecyclerView recyclerViewDelivery;
    private RecyclerView recyclerViewExercise;
    private CommunityAdapter adapterFreeBoard;
    private CommunityAdapter adapterDelivery;
    private CommunityAdapter adapterExercise;
    private List<FreeBoardPost> freeBoardPosts;
    private List<DeliveryPost> deliveryPosts;
    private List<ExercisePost> exercisePosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        btnFreeBoard = view.findViewById(R.id.btn_free_board);
        btnOrderDelivery = view.findViewById(R.id.btn_delivery);
        btnExercise = view.findViewById(R.id.btn_exercise);

        recyclerViewFreeBoard = view.findViewById(R.id.recyclerViewFreeBoard);
        recyclerViewDelivery = view.findViewById(R.id.recyclerViewDelivery);
        recyclerViewExercise = view.findViewById(R.id.recyclerViewExercise);

        freeBoardPosts = new ArrayList<>();
        deliveryPosts = new ArrayList<>();
        exercisePosts = new ArrayList<>();

        adapterFreeBoard = new CommunityAdapter(freeBoardPosts, null, null, this);
        adapterDelivery = new CommunityAdapter(null, deliveryPosts, null, this);
        adapterExercise = new CommunityAdapter(null, null, exercisePosts, this);

        recyclerViewFreeBoard.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFreeBoard.setAdapter(adapterFreeBoard);
        recyclerViewDelivery.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewDelivery.setAdapter(adapterDelivery);
        recyclerViewExercise.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewExercise.setAdapter(adapterExercise);

        loadLatestPosts();

        btnFreeBoard.setOnClickListener(v -> replaceFragment(new FragmentFreeBoard()));
        btnOrderDelivery.setOnClickListener(v -> replaceFragment(new FragmentOrderDelivery()));
        btnExercise.setOnClickListener(v -> replaceFragment(new FragmentExercise()));

        return view;
    }

    void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containers, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadLatestPosts() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("freeboard_posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    freeBoardPosts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        FreeBoardPost post = document.toObject(FreeBoardPost.class);
                        freeBoardPosts.add(post);
                    }
                    adapterFreeBoard.notifyDataSetChanged();
                });

        firestore.collection("deliveryPosts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    deliveryPosts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        DeliveryPost post = document.toObject(DeliveryPost.class);
                        deliveryPosts.add(post);
                    }
                    adapterDelivery.notifyDataSetChanged();
                });

        firestore.collection("exercise_posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    exercisePosts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ExercisePost post = document.toObject(ExercisePost.class);
                        exercisePosts.add(post);
                    }
                    adapterExercise.notifyDataSetChanged();
                });
    }
}
