package com.example.roommade;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentExerciseChat extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private EditText editTextMessage;
    private Button buttonSend;
    private String currentUserId;
    private String postId;
    private ImageButton btnBack;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration messageListener;
    private String userId;
    private List<String> participantIds = new ArrayList<>();

    public FragmentExerciseChat() {
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args != null) {
            postId = args.getString("postId");
            Log.d("FragmentExerciseChat", "postId set: " + postId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_chat);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        btnBack = view.findViewById(R.id.btn_back);

        loadPostData();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageAdapter(getActivity(), messages, currentUserId, userId, participantIds);
        recyclerView.setAdapter(adapter);

        buttonSend.setOnClickListener(v -> {
            String messageText = editTextMessage.getText().toString();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            }
        });

        btnBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void sendMessage(String messageText) {
        if (postId == null) {
            Log.e("FragmentExerciseChat", "postId is null. Cannot send message.");
            return;
        }

        long timestamp = System.currentTimeMillis();
        Message message = new Message(currentUserId, messageText, timestamp);
        messages.add(message);
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
        editTextMessage.setText("");

        sendExerciseMessageToFirestore(message);
    }

    private void loadPostData() {
        if (postId == null) {
            Log.e("FragmentExerciseChat", "postId is null when loading post data.");
            return;
        }

        db.collection("exercise_posts").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userId = documentSnapshot.getString("userId");
                        participantIds = (List<String>) documentSnapshot.get("participantIds");
                        adapter = new MessageAdapter(getActivity(), messages, currentUserId, userId, participantIds);
                        recyclerView.setAdapter(adapter);

                        loadMessagesFromFirestore();
                    }
                })
                .addOnFailureListener(e -> Log.e("FragmentExerciseChat", "Error getting document: ", e));
    }

    private void loadMessagesFromFirestore() {
        if (postId == null) {
            Log.e("FragmentExerciseChat", "postId is null when loading messages.");
            return;
        }

        messageListener = db.collection("exercise_posts")
                .document(postId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        return;
                    }
                    if (snapshot != null && !snapshot.isEmpty()) {
                        messages.clear();
                        for (QueryDocumentSnapshot document : snapshot) {
                            String senderId = document.getString("senderId");
                            String messageText = document.getString("message");
                            long timestamp = document.getLong("timestamp");
                            Message message = new Message(senderId, messageText, timestamp);
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messages.size() - 1);
                    }
                });
    }

    private void sendExerciseMessageToFirestore(Message message) {
        db.collection("exercise_posts")
                .document(postId)
                .collection("messages")
                .add(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            messageListener.remove();
        }
    }
}