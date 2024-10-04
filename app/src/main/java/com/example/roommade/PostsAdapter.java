package com.example.roommade;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<FreeBoardPost> postList;
    private FragmentFreeBoard fragmentFreeBoard;

    public PostsAdapter(List<FreeBoardPost> postList, FragmentFreeBoard fragmentFreeBoard) {
        this.postList = postList;
        this.fragmentFreeBoard = fragmentFreeBoard;
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

        holder.itemView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("postId", post.getPostId());
            args.putString("title", post.getTitle());
            args.putString("content", post.getContent());
            args.putString("userId", post.getUserId());
            args.putLong("timestamp", post.getTimestamp());

            FragmentFreeBoardPost fragmentFreeBoardPost = new FragmentFreeBoardPost();
            fragmentFreeBoardPost.setArguments(args);

            fragmentFreeBoard.getParentFragmentManager()
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewContent;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
        }
    }
    
}



