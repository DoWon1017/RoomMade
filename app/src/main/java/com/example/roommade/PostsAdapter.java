package com.example.roommade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<FreeBoardPost> postList;
    private Fragment fragment;
    private List<Boolean> checkedItems; // 체크 상태를 저장할 리스트
    private boolean isDeleteMode = false; // 삭제 모드 상태

    public PostsAdapter(List<FreeBoardPost> postList, Fragment fragment) {
        this.postList = postList;
        this.fragment = fragment;
        checkedItems = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            checkedItems.add(false);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_freeboarditempost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FreeBoardPost post = postList.get(position);
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewContent.setText(post.getContent());

        if (isDeleteMode) {
            holder.checkBoxTitle.setVisibility(View.VISIBLE);
            holder.checkBoxTitle.setChecked(checkedItems.get(position));
        } else {
            holder.checkBoxTitle.setVisibility(View.GONE);
        }

        holder.checkBoxTitle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isDeleteMode) {
                checkedItems.set(position, isChecked);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("postId", post.getPostId());
            args.putString("title", post.getTitle());
            args.putString("content", post.getContent());
            args.putString("userId", post.getUserId());
            args.putLong("timestamp", post.getTimestamp());
            args.putString("imageUrl", post.getImageUrl());

            FragmentFreeBoardPost fragmentFreeBoardPost = new FragmentFreeBoardPost();
            fragmentFreeBoardPost.setArguments(args);

            fragment.getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containers, fragmentFreeBoardPost)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public List<FreeBoardPost> getSelectedPosts() {
        List<FreeBoardPost> selectedPosts = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            if (checkedItems.get(i)) {
                selectedPosts.add(postList.get(i));
            }
        }
        return selectedPosts;
    }

    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode; // 삭제 모드 상태 설정
        notifyDataSetChanged(); // 데이터 변경 알림
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewContent;
        CheckBox checkBoxTitle; // 체크박스 추가

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            checkBoxTitle = itemView.findViewById(R.id.checkBoxTitle); // 체크박스 초기화
        }
    }
}
