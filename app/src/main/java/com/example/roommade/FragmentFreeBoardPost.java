package com.example.roommade;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class FragmentFreeBoardPost extends Fragment {

    private FreeBoardPost post;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_freeboardpost, container, false);

        Bundle args = getArguments();
        if (args != null) {
            post = new FreeBoardPost(
                    args.getString("title"),
                    args.getString("content"),
                    args.getString("userId"),
                    args.getLong("timestamp")
            );

            TextView textViewTitle = view.findViewById(R.id.textViewTitle);
            TextView textViewContent = view.findViewById(R.id.textViewContent);
            TextView textViewAuthor = view.findViewById(R.id.textViewAuthor);
            TextView textViewTimestamp = view.findViewById(R.id.textViewTimestamp);

            textViewTitle.setText(post.getTitle());
            textViewContent.setText(post.getContent());
            textViewAuthor.setText("익명");
            textViewTimestamp.setText(formatDate(post.getTimestamp()));
        }

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentFreeBoard fragmentFreeBoard = new FragmentFreeBoard();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containers, fragmentFreeBoard)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private String formatDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
}



