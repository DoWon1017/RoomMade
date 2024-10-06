package com.example.roommade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import java.util.List;

public class ExercisePostAdapter extends RecyclerView.Adapter<ExercisePostAdapter.ViewHolder> {

    private List<ExercisePost> exercisePostList;
    private FragmentExercise fragmentExercise;

    public ExercisePostAdapter(List<ExercisePost> postList, FragmentExercise fragmentExercise) {
        this.exercisePostList = postList;
        this.fragmentExercise = fragmentExercise;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercisepostlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExercisePost post = exercisePostList.get(position);
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewContent.setText(post.getContent());
        holder.textViewParticipants.setText("참여 인원: " + post.getCurrentParticipants() + "/" + post.getMaxParticipants());

        holder.itemView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", post.getTitle());
            args.putString("content", post.getContent());
            args.putString("userId", post.getUserId());
            args.putLong("timestamp", post.getTimestamp());
            args.putInt("maxParticipants", post.getMaxParticipants());
            args.putInt("currentParticipants", post.getCurrentParticipants());

            FragmentExercisePost fragmentExercisePost = new FragmentExercisePost();
            fragmentExercisePost.setArguments(args);

            fragmentExercise.getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containers, fragmentExercisePost)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return exercisePostList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewContent;
        TextView textViewParticipants;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewExerciseTitle);
            textViewContent = itemView.findViewById(R.id.textViewExerciseContent);
            textViewParticipants = itemView.findViewById(R.id.textViewExerciseParticipants);
        }
    }
}
