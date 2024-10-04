package com.example.roommade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DeliveryPostAdapter extends RecyclerView.Adapter<DeliveryPostAdapter.ViewHolder> {

    private List<DeliveryPost> deliveryPostList;

    public DeliveryPostAdapter(List<DeliveryPost> deliveryPostList) {
        this.deliveryPostList = deliveryPostList;
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

        int currentParticipants = post.getCurrentParticipants();
        int maxParticipants = post.getMaxParticipants();
        String participantsText = currentParticipants + "/" + maxParticipants;

        holder.textViewParticipants.setText(participantsText);
    }


    @Override
    public int getItemCount() {
        return deliveryPostList.size();
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
