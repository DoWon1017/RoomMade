package com.example.roommade;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPostAdapter extends RecyclerView.Adapter<DeliveryPostAdapter.ViewHolder> {

    private List<DeliveryPost> deliveryPostList;
    private Context context;
    private String currentUserId;
    private FirebaseFirestore db;
    private List<Boolean> checkedItems;
    private boolean isDeleteMode = false; // 삭제 모드 상태

    public DeliveryPostAdapter(Context context, List<DeliveryPost> deliveryPostList, String currentUserId, FirebaseFirestore db) {
        this.context = context;
        this.deliveryPostList = deliveryPostList;
        this.currentUserId = currentUserId;
        this.db = db;
        checkedItems = new ArrayList<>();
        for (int i = 0; i < deliveryPostList.size(); i++) {
            checkedItems.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deliverypostlist, parent, false);
        return new ViewHolder(view);
    }

    private boolean isPostClickable(DeliveryPost post) {
        boolean isActiveAndNotFull = post.isActive() && post.getCurrentParticipants() < post.getMaxParticipants();
        List<String> participantIds = post.getParticipantIds();
        boolean isParticipant = participantIds != null && participantIds.contains(currentUserId);
        return isActiveAndNotFull || post.getUserId().equals(currentUserId) || isParticipant;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeliveryPost post = deliveryPostList.get(position);
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewRemainingTime.setText(post.getRemainingTime());

        String participantsText = post.getCurrentParticipants() + "/" + post.getMaxParticipants();
        holder.textViewParticipants.setText(participantsText);

        if (isDeleteMode) {
            holder.checkBoxDeliveryTitle.setVisibility(View.VISIBLE);
            holder.checkBoxDeliveryTitle.setChecked(checkedItems.get(position));
        } else {
            holder.checkBoxDeliveryTitle.setVisibility(View.GONE);
        }

        holder.checkBoxDeliveryTitle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isDeleteMode) {
                checkedItems.set(position, isChecked);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (isPostClickable(post)) {
                List<String> participantIds = post.getParticipantIds();
                if (post.getUserId().equals(currentUserId) || (participantIds != null && participantIds.contains(currentUserId))) {
                    if (context instanceof FragmentActivity) {
                        navigateToChatRoom(post, ((FragmentActivity) context).getSupportFragmentManager());
                    }
                } else {
                    showConfirmationDialog(post);
                }
            } else {
                showErrorDialog("모집이 종료된 채팅방입니다.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return deliveryPostList.size();
    }

    public List<DeliveryPost> getSelectedPosts() {
        List<DeliveryPost> selectedPosts = new ArrayList<>();
        for (int i = 0; i < deliveryPostList.size(); i++) {
            if (checkedItems.get(i)) {
                selectedPosts.add(deliveryPostList.get(i));
            }
        }
        return selectedPosts;
    }

    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        notifyDataSetChanged();
    }

    private void showConfirmationDialog(DeliveryPost post) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("배달 참여")
                    .setMessage("같이 배달 하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> {
                        if (context instanceof FragmentActivity) {
                            addParticipant(post, currentUserId);
                            navigateToChatRoom(post, ((FragmentActivity) context).getSupportFragmentManager());
                        }
                    })
                    .setNegativeButton("아니요", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void addParticipant(DeliveryPost post, String userId) {
        if (db != null) {
            db.collection("deliveryPosts").document(post.getPostId())
                    .update("participantIds", FieldValue.arrayUnion(userId),
                            "currentParticipants", FieldValue.increment(1))
                    .addOnSuccessListener(aVoid -> {
                        List<String> participantIds = post.getParticipantIds();
                        if (participantIds == null) {
                            participantIds = new ArrayList<>();
                        }
                        participantIds.add(userId);
                        post.setCurrentParticipants(post.getCurrentParticipants() + 1);

                        int position = deliveryPostList.indexOf(post);
                        if (position >= 0) {
                            notifyItemChanged(position);
                        }
                    });
        }
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

    private void showErrorDialog(String message) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("알림")
                    .setMessage(message)
                    .setPositiveButton("확인", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewRemainingTime;
        TextView textViewParticipants;
        CheckBox checkBoxDeliveryTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewDeliveryTitle);
            textViewRemainingTime = itemView.findViewById(R.id.textViewRemainingTime);
            textViewParticipants = itemView.findViewById(R.id.textViewParticipants);
            checkBoxDeliveryTitle = itemView.findViewById(R.id.checkBoxDeliveryTitle);
        }
    }
}
