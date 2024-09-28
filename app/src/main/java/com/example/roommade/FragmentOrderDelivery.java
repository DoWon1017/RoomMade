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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FragmentOrderDelivery extends Fragment {

    private RecyclerView recyclerView;
    private DeliveryPostAdapter adapter;
    private List<DeliveryPost> deliveryPosts = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_delivery, container, false);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
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

        adapter = new DeliveryPostAdapter(deliveryPosts);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadDeliveryPosts();

        return view;
    }

    private void loadDeliveryPosts() {
        db.collection("deliveryPosts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING) // 최신순으로 정렬
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deliveryPosts.clear();
                        long currentTime = System.currentTimeMillis();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String remainingTime = document.getString("remainingTime");
                            long startTime = document.getLong("startTime");
                            long timestamp = document.getLong("timestamp"); // 타임스탬프 추가

                            long remainingMillis = getRemainingTimeInMillis(startTime, remainingTime);
                            if (remainingMillis > 0) {
                                DeliveryPost post = new DeliveryPost(title, formatRemainingTime(remainingMillis), timestamp); // 타임스탬프 추가
                                deliveryPosts.add(post);
                                updateRemainingTime(post, startTime, remainingTime);
                            } else {
                                deliveryPosts.add(new DeliveryPost(title, "모집 종료", timestamp)); // 타임스탬프 추가
                            }
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
                    // 시간이 다 된 경우 처리
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




