package com.example.roommade;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DeliveryPostAdapter extends RecyclerView.Adapter<DeliveryPostAdapter.ViewHolder> {

    private List<DeliveryPost> deliveryPostList;
    private Context context;
    private String currentUserId;

    public DeliveryPostAdapter(Context context, List<DeliveryPost> deliveryPostList, String currentUserId) {
        this.context = context;
        this.deliveryPostList = deliveryPostList;
        this.currentUserId = currentUserId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deliverypostlist, parent, false);
        return new ViewHolder(view);
    }

    private boolean isPostClickable(DeliveryPost post, String currentUserId) {
        boolean isActiveAndNotFull = post.isActive() && post.getCurrentParticipants() < post.getMaxParticipants();
        boolean isParticipant = post.getParticipantIds() != null && post.getParticipantIds().contains(currentUserId);
        return isActiveAndNotFull || post.getUserId().equals(currentUserId) || isParticipant;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeliveryPost post = deliveryPostList.get(position);
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewRemainingTime.setText(post.getRemainingTime());

        int currentParticipants = post.getCurrentParticipants();
        int maxParticipants = post.getMaxParticipants();
        String participantsText = currentParticipants + "/" + maxParticipants;
        holder.textViewParticipants.setText(participantsText);

        holder.itemView.setOnClickListener(v -> {
            if (isPostClickable(post, currentUserId)) {
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

    private void showConfirmationDialog(DeliveryPost post) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("배달 참여")
                    .setMessage("같이 배달 하시겠습니까?")
                    .setPositiveButton("예", (dialog, which) -> {
                        if (context instanceof FragmentActivity) {
                            navigateToChatRoom(post, ((FragmentActivity) context).getSupportFragmentManager());
                        }
                    })
                    .setNegativeButton("아니요", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
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
