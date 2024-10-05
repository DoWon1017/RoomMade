package com.example.roommade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {
    private List<FreeBoardPost> freeBoardPosts;
    private List<DeliveryPost> deliveryPosts;
    private List<ExercisePost> exercisePosts;
    private Fragment fragment;

    public CommunityAdapter(List<FreeBoardPost> freeBoardPosts, List<DeliveryPost> deliveryPosts, List<ExercisePost> exercisePosts, Fragment fragment) {
        this.freeBoardPosts = freeBoardPosts;
        this.deliveryPosts = deliveryPosts;
        this.exercisePosts = exercisePosts;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_minipost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (freeBoardPosts != null && position < freeBoardPosts.size()) {
            FreeBoardPost post = freeBoardPosts.get(position);
            holder.titleTextView.setText(post.getTitle());
            holder.timeTextView.setText(formatTimestamp(post.getTimestamp()));
            holder.itemView.setOnClickListener(v -> {
                if (fragment instanceof FragmentCommunity) {
                    ((FragmentCommunity) fragment).replaceFragment(new FragmentFreeBoard());
                }
            });
        } else if (deliveryPosts != null && position < deliveryPosts.size()) {
            DeliveryPost post = deliveryPosts.get(position);
            holder.titleTextView.setText(post.getTitle());
            holder.timeTextView.setText(formatTimestamp(post.getTimestamp()));
            holder.itemView.setOnClickListener(v -> {
                if (fragment instanceof FragmentCommunity) {
                    ((FragmentCommunity) fragment).replaceFragment(new FragmentOrderDelivery());
                }
            });
        } else if (exercisePosts != null && position < exercisePosts.size()) {
            ExercisePost post = exercisePosts.get(position);
            holder.titleTextView.setText(post.getTitle());
            holder.timeTextView.setText(formatTimestamp(post.getTimestamp()));
            holder.itemView.setOnClickListener(v -> {
                if (fragment instanceof FragmentCommunity) {
                    ((FragmentCommunity) fragment).replaceFragment(new FragmentExercise());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (freeBoardPosts != null) count += freeBoardPosts.size();
        if (deliveryPosts != null) count += deliveryPosts.size();
        if (exercisePosts != null) count += exercisePosts.size();
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView timeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewPostTitle);
            timeTextView = itemView.findViewById(R.id.textViewPostTime);
        }
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}
