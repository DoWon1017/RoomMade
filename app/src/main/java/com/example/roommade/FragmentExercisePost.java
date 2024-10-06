package com.example.roommade;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class FragmentExercisePost extends Fragment {

    private ExercisePost exercisepost;
    private FirebaseFirestore db;
    private String currentUserId;
    private Button btnJoinChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercisepost, container, false);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle args = getArguments();
        if (args != null) {
            exercisepost = new ExercisePost(
                    args.getString("postId"),
                    args.getString("title"),
                    args.getString("content"),
                    args.getString("userId"),
                    args.getLong("timestamp"),
                    args.getInt("maxParticipants"),
                    args.getInt("currentParticipants"),
                    args.getStringArrayList("participantIds") != null ? args.getStringArrayList("participantIds") : new ArrayList<>()
            );

            // UI 요소 설정
            TextView textViewTitle = view.findViewById(R.id.textViewExerciseTitle);
            TextView textViewContent = view.findViewById(R.id.textViewExerciseContent);
            TextView textViewAuthor = view.findViewById(R.id.textViewExerciseAuthor);
            TextView textViewTimestamp = view.findViewById(R.id.textViewExerciseTimestamp);
            TextView textViewParticipants = view.findViewById(R.id.textViewExerciseParticipants);
            btnJoinChat = view.findViewById(R.id.btn_join_chat);

            textViewTitle.setText(exercisepost.getTitle());
            textViewContent.setText(exercisepost.getContent());
            textViewAuthor.setText("익명");
            textViewTimestamp.setText(formatDate(exercisepost.getTimestamp()));
            textViewParticipants.setText("참여 인원: " + exercisepost.getCurrentParticipants() + "/" + exercisepost.getMaxParticipants());

            updateJoinButtonState();

            btnJoinChat.setOnClickListener(v -> {
                if (exercisepost.getUserId().equals(currentUserId)) {
                    navigateToChatRoom(exercisepost);
                } else {
                    if (exercisepost.getCurrentParticipants() < exercisepost.getMaxParticipants()) {
                        showJoinChatDialog();
                    } else {
                        showFullDialog();
                    }
                }
            });
        }

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            FragmentExercise fragmentExercise = new FragmentExercise();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containers, fragmentExercise)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void updateJoinButtonState() {
        if (exercisepost.getCurrentParticipants() >= exercisepost.getMaxParticipants()) {
            btnJoinChat.setEnabled(false); // 버튼 비활성화
        }
    }

    private void showJoinChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("채팅방으로 이동하시겠습니까?");
        builder.setMessage("채팅방에 참여하시겠습니까?");
        builder.setPositiveButton("예", (dialog, which) -> {
            joinPostAndOpenChat();
        });
        builder.setNegativeButton("아니오", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showFullDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("모집 마감");
        builder.setMessage("이미 마감된 모집입니다.");
        builder.setPositiveButton("확인", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void joinPostAndOpenChat() {
        exercisepost.setCurrentParticipants(exercisepost.getCurrentParticipants() + 1);
        addParticipantToPost();
        updateParticipantsInDatabase();
        navigateToChatRoom(exercisepost);
    }

    private void addParticipantToPost() {
        List<String> participantIds = exercisepost.getParticipantIds();
        if (participantIds == null) {
            participantIds = new ArrayList<>();
        }
        participantIds.add(currentUserId);
        exercisepost.setParticipantIds(participantIds);
    }

    private void updateParticipantsInDatabase() {
        DocumentReference postRef = db.collection("exercise_posts").document(exercisepost.getPostId());
        postRef.update("currentParticipants", exercisepost.getCurrentParticipants(),
                        "participantIds", exercisepost.getParticipantIds())
                .addOnSuccessListener(aVoid -> {
                    updateJoinButtonState();
                });
    }

    private void navigateToChatRoom(ExercisePost post) {
        FragmentExerciseChat fragmentExerciseChat = new FragmentExerciseChat();

        Bundle args = new Bundle();
        args.putString("postId", post.getPostId());
        fragmentExerciseChat.setCurrentUserId(currentUserId);  // currentUserId 전달
        fragmentExerciseChat.setArguments(args);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containers, fragmentExerciseChat)
                .addToBackStack(null)
                .commit();
    }


    private String formatDate(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        java.util.Date date = new java.util.Date(timestamp);
        return sdf.format(date);
    }
}
