package com.example.roommade;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class FragmentExercisePost extends Fragment {

    private ExercisePost post;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercisepost, container, false);

        Bundle args = getArguments();
        if (args != null) {
            post = new ExercisePost(
                    args.getString("title"),
                    args.getString("content"),
                    args.getString("userId"),
                    args.getLong("timestamp"),
                    args.getInt("maxParticipants"),
                    args.getInt("currentParticipants")  // 현재 참여 인원
            );

            TextView textViewTitle = view.findViewById(R.id.textViewExerciseTitle);
            TextView textViewContent = view.findViewById(R.id.textViewExerciseContent);
            TextView textViewAuthor = view.findViewById(R.id.textViewExerciseAuthor);
            TextView textViewTimestamp = view.findViewById(R.id.textViewExerciseTimestamp);
            TextView textViewParticipants = view.findViewById(R.id.textViewExerciseParticipants);

            textViewTitle.setText(post.getTitle());
            textViewContent.setText(post.getContent());
            textViewAuthor.setText("익명");
            textViewTimestamp.setText(formatDate(post.getTimestamp()));
            textViewParticipants.setText("참여 인원: " + post.getCurrentParticipants() + "/" + post.getMaxParticipants());
        }

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentExercise fragmentExercise = new FragmentExercise();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containers, fragmentExercise)
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
