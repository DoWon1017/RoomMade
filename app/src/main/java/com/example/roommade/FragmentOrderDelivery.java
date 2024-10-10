package com.example.roommade;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FragmentOrderDelivery extends Fragment {

    private RecyclerView recyclerView;
    private DeliveryPostAdapter adapter;
    private List<DeliveryPost> deliveryPosts = new ArrayList<>();
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_delivery, container, false);
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCommunity fragmentCommunity = new FragmentCommunity();
                getActivity().getSupportFragmentManager()
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
                FragmentWriteDeliveryPost fragmentWriteDeliveryPost = new FragmentWriteDeliveryPost();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.containers, fragmentWriteDeliveryPost)
                        .addToBackStack(null)
                        .commit();
            }
        });

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new DeliveryPostAdapter(getContext(), deliveryPosts, currentUserId, db);
        recyclerView.setAdapter(adapter);

        loadDeliveryPosts();

        return view;
    }

    private void loadDeliveryPosts() {
        db.collection("deliveryPosts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deliveryPosts.clear();
                        long currentTime = System.currentTimeMillis();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String postId = document.getId();
                            String title = document.getString("title");
                            String remainingTime = document.getString("remainingTime");
                            long startTime = document.getLong("startTime");
                            long timestamp = document.getLong("timestamp");
                            String userId = document.getString("userId");
                            int maxParticipants = document.getLong("maxParticipants").intValue();
                            int currentParticipants = document.getLong("currentParticipants").intValue();

                            List<String> participantIds = (List<String>) document.get("participantIds");

                            long remainingMillis = getRemainingTimeInMillis(startTime, remainingTime);
                            boolean isActive = remainingMillis > 0 && currentParticipants < maxParticipants;

                            DeliveryPost post = new DeliveryPost(
                                    postId,
                                    title,
                                    formatRemainingTime(remainingMillis),
                                    timestamp,
                                    userId,
                                    maxParticipants,
                                    currentParticipants,
                                    isActive,
                                    participantIds
                            );

                            deliveryPosts.add(post);
                            updateRemainingTime(post, startTime, remainingTime);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void updateRemainingTime(DeliveryPost post, long startTime, String remainingTime) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long remainingMillis = getRemainingTimeInMillis(startTime, remainingTime);
                if (remainingMillis > 0) {
                    post.setRemainingTime(formatRemainingTime(remainingMillis));
                    adapter.notifyDataSetChanged();
                    handler.postDelayed(this, 1000);
                } else {
                    post.setRemainingTime("모집 종료");
                    adapter.notifyDataSetChanged();
                }
            }
        };
        handler.post(runnable);
    }

    private String formatRemainingTime(long seconds) {
        long minutes = seconds / 60000;
        return minutes + "분";
    }

    private long getRemainingTimeInMillis(long startTime, String remainingTime) {
        int minutes = convertToMinutes(remainingTime);
        long durationInMillis = minutes * 60 * 1000;
        long currentTime = System.currentTimeMillis();
        return durationInMillis - (currentTime - startTime);
    }

    private int convertToMinutes(String remainingTime) {
        switch (remainingTime) {
            case "10분": return 10;
            case "15분": return 15;
            case "20분": return 20;
            case "25분": return 25;
            case "30분": return 30;
            case "35분": return 35;
            case "40분": return 40;
            case "45분": return 45;
            case "50분": return 50;
            case "55분": return 55;
            case "1시간": return 60;
            default: return 0;
        }
    }
}