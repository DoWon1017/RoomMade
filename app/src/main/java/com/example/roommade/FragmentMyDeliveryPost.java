package com.example.roommade;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FragmentMyDeliveryPost extends Fragment {

    private RecyclerView recyclerView;
    private DeliveryPostAdapter deliveryPostsAdapter;
    private List<DeliveryPost> deliveryPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mydelivery, container, false);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        deliveryPosts = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // 어댑터 설정
        deliveryPostsAdapter = new DeliveryPostAdapter(getContext(), deliveryPosts, FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseFirestore.getInstance());
        recyclerView.setAdapter(deliveryPostsAdapter);

        loadDeliveryPosts();

        return view;
    }

    private void loadDeliveryPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        db.collection("deliveryPosts")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deliveryPosts.clear();
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

                        deliveryPosts.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));

                        deliveryPostsAdapter.notifyDataSetChanged();

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
                    deliveryPostsAdapter.notifyDataSetChanged();
                    handler.postDelayed(this, 1000);
                } else {
                    post.setRemainingTime("모집 종료");
                    deliveryPostsAdapter.notifyDataSetChanged();
                }
            }
        };
        handler.post(runnable);
    }

    private String formatRemainingTime(long millis) {
        long minutes = millis / 60000;
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
