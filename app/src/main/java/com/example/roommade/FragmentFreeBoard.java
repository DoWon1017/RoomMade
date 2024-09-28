package com.example.roommade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FragmentFreeBoard extends Fragment {

    private RecyclerView recyclerViewPosts;
    private PostsAdapter postsAdapter;
    private List<FreeBoardPost> postList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freeboard, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(postList, this);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewPosts.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerViewPosts.addItemDecoration(dividerItemDecoration);

        recyclerViewPosts.setAdapter(postsAdapter);

        loadPosts();

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        Button btnWritePost = view.findViewById(R.id.btn_write_post);
        btnWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentWritePost fragmentWritePost = new FragmentWritePost();
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containers, fragmentWritePost)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void loadPosts() {
        db.collection("freeboard_posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            postList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FreeBoardPost post = document.toObject(FreeBoardPost.class);
                                postList.add(post);
                            }
                            postsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}






