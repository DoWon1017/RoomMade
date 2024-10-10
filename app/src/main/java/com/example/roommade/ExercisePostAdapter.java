package com.example.roommade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ExercisePostAdapter extends RecyclerView.Adapter<ExercisePostAdapter.ViewHolder> {

    private List<ExercisePost> exercisePostList;
    private Fragment fragment;
    private List<Boolean> checkedItems;
    private boolean isDeleteMode = false;
    private String currentUserId;

    // 생성자에서 currentUserId 전달받음
    public ExercisePostAdapter(List<ExercisePost> postList, Fragment fragment, String currentUserId) {
        this.exercisePostList = postList;
        this.fragment = fragment;
        this.currentUserId = currentUserId;
        checkedItems = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            checkedItems.add(false);
        }
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

        // 체크 박스 처리
        if (isDeleteMode) {
            holder.checkBoxExerciseTitle.setVisibility(View.VISIBLE);
            holder.checkBoxExerciseTitle.setChecked(checkedItems.get(position));
        } else {
            holder.checkBoxExerciseTitle.setVisibility(View.GONE);
        }

        holder.checkBoxExerciseTitle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isDeleteMode) {
                checkedItems.set(position, isChecked);
            }
        });

        // buttonShortcutJoinChat 표시
        if (post.getUserId().equals(currentUserId) || (post.getParticipantIds() != null && post.getParticipantIds().contains(currentUserId))) {
            holder.buttonShortcutJoinChat.setVisibility(View.VISIBLE);
            holder.buttonShortcutJoinChat.setOnClickListener(v -> {
                navigateToChatRoom(post);
            });
        } else {
            holder.buttonShortcutJoinChat.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", post.getTitle());
            args.putString("content", post.getContent());
            args.putString("userId", post.getUserId());
            args.putLong("timestamp", post.getTimestamp());
            args.putInt("maxParticipants", post.getMaxParticipants());
            args.putInt("currentParticipants", post.getCurrentParticipants());
            args.putString("postId", post.getPostId());

            FragmentExercisePost fragmentExercisePost = new FragmentExercisePost();
            fragmentExercisePost.setArguments(args);

            fragment.getParentFragmentManager()
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

    // 채팅방으로 이동하는 메소드
    private void navigateToChatRoom(ExercisePost post) {
        FragmentExerciseChat fragmentExerciseChat = new FragmentExerciseChat();

        Bundle args = new Bundle();
        args.putString("postId", post.getPostId());
        fragmentExerciseChat.setCurrentUserId(currentUserId);
        fragmentExerciseChat.setArguments(args);

        fragment.getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.containers, fragmentExerciseChat)
                .addToBackStack(null)
                .commit();
    }

    // 선택된 게시글 목록 가져오기
    public List<ExercisePost> getSelectedPosts() {
        List<ExercisePost> selectedPosts = new ArrayList<>();
        for (int i = 0; i < exercisePostList.size(); i++) {
            if (checkedItems.get(i)) {
                selectedPosts.add(exercisePostList.get(i));
            }
        }
        return selectedPosts;
    }

    // 삭제 모드 설정 메소드
    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewContent;
        TextView textViewParticipants;
        CheckBox checkBoxExerciseTitle;
        Button buttonShortcutJoinChat;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewExerciseTitle);
            textViewContent = itemView.findViewById(R.id.textViewExerciseContent);
            textViewParticipants = itemView.findViewById(R.id.textViewExerciseParticipants);
            checkBoxExerciseTitle = itemView.findViewById(R.id.checkBoxExerciseTitle);
            buttonShortcutJoinChat = itemView.findViewById(R.id.buttonShortcutJoinChat);
        }
    }
}
