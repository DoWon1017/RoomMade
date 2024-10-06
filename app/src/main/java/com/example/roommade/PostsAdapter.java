package com.example.roommade;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private List<FreeBoardPost> postList; // 게시물 리스트
    private FragmentFreeBoard fragmentFreeBoard; // 프래그먼트 참조
    private boolean isDeleteMode;  // 삭제 모드 상태

    public PostsAdapter(List<FreeBoardPost> postList, FragmentFreeBoard fragmentFreeBoard) {
        this.postList = postList;
        this.fragmentFreeBoard = fragmentFreeBoard;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // fragment_freeboarditempost.xml 레이아웃으로 뷰 홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_freeboarditempost, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FreeBoardPost post = postList.get(position);
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewContent.setText(post.getContent());
        holder.checkBoxSelectPost.setTag(post.getPostId()); // CheckBox에 postId 설정

        // 체크박스 상태 설정
        holder.checkBoxSelectPost.setChecked(post.isSelected());

        // 삭제 모드일 때 체크박스를 보여주고, 아닐 때 숨김
        holder.checkBoxSelectPost.setVisibility(isDeleteMode ? View.VISIBLE : View.GONE);

        // 체크박스 상태 변경 리스너
        holder.checkBoxSelectPost.setOnCheckedChangeListener(null); // 리스너 초기화
        holder.checkBoxSelectPost.setChecked(post.isSelected()); // 체크박스 상태 다시 설정
        holder.checkBoxSelectPost.setOnCheckedChangeListener((buttonView, isChecked) -> {
            post.setSelected(isChecked); // 체크박스 상태를 FreeBoardPost에 저장
            Log.d("PostsAdapter", "Post ID: " + post.getPostId() + ", Checked: " + isChecked); // 로그 추가
        });

        // 게시글 클릭 시 상세 화면으로 이동
        holder.itemView.setOnClickListener(v -> {
            if (fragmentFreeBoard != null && !isDeleteMode) { // 삭제 모드가 아닐 때만 클릭 가능
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
            } else {
                Log.e("PostsAdapter", "FragmentFreeBoard is null! Cannot navigate.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // 삭제 모드 설정 메소드
    public void setDeleteMode(boolean deleteMode) {
        isDeleteMode = deleteMode;

        // 삭제 모드 해제 시 모든 체크박스 선택 해제
        if (!deleteMode) {
            for (FreeBoardPost post : postList) {
                post.setSelected(false); // 체크박스 선택 해제
            }
        }

        notifyDataSetChanged(); // 데이터 변경을 알림
    }

    // 선택된 포스트 ID 목록을 반환하는 메소드 추가
    public List<String> getSelectedPostIds() {
        List<String> selectedPostIds = new ArrayList<>();
        for (FreeBoardPost post : postList) {
            if (post.isSelected()) {
                selectedPostIds.add(post.getPostId());
            }
        }
        return selectedPostIds;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewContent;
        CheckBox checkBoxSelectPost;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            checkBoxSelectPost = itemView.findViewById(R.id.post_checkbox);
            // 추가 로그
            if (checkBoxSelectPost == null) {
                Log.e("PostsAdapter", "CheckBox is null in ViewHolder");
            }
        }
    }
}
