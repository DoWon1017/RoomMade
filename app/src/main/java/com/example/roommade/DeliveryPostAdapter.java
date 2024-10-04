package com.example.roommade;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPostAdapter extends RecyclerView.Adapter<DeliveryPostAdapter.ViewHolder> {
    private List<DeliveryPost> deliveryPostList = new ArrayList<>();
    private Context context;
    private String currentUserId;
    private FirebaseFirestore db;

    public DeliveryPostAdapter(Context context, String currentUserId, FirebaseFirestore db) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.db = db;

        // Firestore에서 데이터 가져오기
        fetchDeliveryPosts();
    }

    private void fetchDeliveryPosts() {
        db.collection("deliveryPosts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deliveryPostList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String postId = document.getId();
                            String title = document.getString("title");
                            String remainingTime = document.getString("remainingTime");
                            long startTime = document.getLong("startTime");
                            long timestamp = document.getLong("timestamp");
                            String userId = document.getString("userId");

                            int maxParticipants = document.getLong("maxParticipants") != null ? document.getLong("maxParticipants").intValue() : 0;
                            int currentParticipants = document.getLong("currentParticipants") != null ? document.getLong("currentParticipants").intValue() : 0;

                            List<String> participantIds = (List<String>) document.get("participantIds");

                            boolean isActive = document.getBoolean("isActive") != null && document.getBoolean("isActive"); // null 체크 추가

                            DeliveryPost post = new DeliveryPost(
                                    postId,
                                    userId,
                                    title,
                                    currentParticipants,
                                    maxParticipants,
                                    remainingTime,
                                    timestamp,
                                    isActive,
                                    participantIds
                            );

                            deliveryPostList.add(post);
                        }
                        notifyDataSetChanged();
                    }
                });
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deliverypostlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeliveryPost post = deliveryPostList.get(position);
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewRemainingTime.setText(post.getRemainingTime());

        // 참가자 수 텍스트 설정
        int currentParticipants = post.getCurrentParticipants(); // null 체크 제거
        int maxParticipants = post.getMaxParticipants(); // null 체크 제거
        holder.textViewParticipants.setText(currentParticipants + "/" + maxParticipants);

        holder.itemView.setOnClickListener(v -> {
            if (isPostClickable(post)) {
                if (post.getUserId().equals(currentUserId) || post.getParticipantIds().contains(currentUserId)) {
                    if (context instanceof FragmentActivity) {
                        navigateToChatRoom(post, ((FragmentActivity) context).getSupportFragmentManager());
                    }
                } else {
                    showConfirmationDialog(post);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return deliveryPostList.size();
    }

    private boolean isPostClickable(DeliveryPost post) {
        boolean isActiveAndNotFull = post.isActive() && (post.getCurrentParticipants() < post.getMaxParticipants());
        boolean isParticipant = post.getParticipantIds() != null && post.getParticipantIds().contains(currentUserId);
        return isActiveAndNotFull || post.getUserId().equals(currentUserId) || isParticipant;
    }

    private void showConfirmationDialog(DeliveryPost post) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("배달 참여")
                    .setMessage("같이 배달 하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> {
                        addParticipant(post, currentUserId);
                    })
                    .setNegativeButton("아니요", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void addParticipant(DeliveryPost post, String userId) {
        if (post.getParticipantIds() == null) {
            post.setParticipantIds(new ArrayList<>());
        }

        db.collection("deliveryPosts").document(post.getPostId())
                .update("participantIds", FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> {
                    post.getParticipantIds().add(userId);
                    post.setCurrentParticipants(post.getCurrentParticipants() + 1);
                    notifyDataSetChanged();

                    // 참가자 추가 후 채팅방으로 이동
                    if (context instanceof FragmentActivity) {
                        navigateToChatRoom(post, ((FragmentActivity) context).getSupportFragmentManager());
                    }
                });
    }

    private void navigateToChatRoom(DeliveryPost post, FragmentManager fragmentManager) {
        FragmentChat fragmentChat = new FragmentChat(currentUserId, post.getPostId());

        Bundle args = new Bundle();
        args.putString("postId", post.getPostId());
        fragmentChat.setArguments(args);

        fragmentManager.beginTransaction()
                .replace(R.id.containers, fragmentChat)
                .addToBackStack(null)
                .commit();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewRemainingTime;
        TextView textViewParticipants;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewDeliveryTitle);
            textViewRemainingTime = itemView.findViewById(R.id.textViewRemainingTime);
            textViewParticipants = itemView.findViewById(R.id.textViewParticipants);
        }
    }
}
