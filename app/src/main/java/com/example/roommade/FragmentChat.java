package com.example.roommade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentChat extends Fragment {

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

    public FragmentChat(String currentUserId, String postId) {
        this.currentUserId = currentUserId;
        this.postId = postId;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args != null) {
            postId = args.getString("postId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_chat);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        btnBack = view.findViewById(R.id.btn_back);
        adapter = new MessageAdapter(messages, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        loadMessagesFromFirestore();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString();
                if (!messageText.isEmpty()) {
                    long timestamp = System.currentTimeMillis();
                    Message message = new Message(currentUserId, messageText, timestamp);
                    messages.add(message);
                    adapter.notifyItemInserted(messages.size() - 1);
                    recyclerView.scrollToPosition(messages.size() - 1);
                    editTextMessage.setText("");
                    sendMessageToFirestore(message);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentOrderDelivery fragmentOrderDelivery = new FragmentOrderDelivery();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.containers, fragmentOrderDelivery);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void loadMessagesFromFirestore() {
        messageListener = db.collection("chatrooms")
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

    private void sendMessageToFirestore(Message message) {
        db.collection("chatrooms").document(postId)
                .collection("messages").add(message)
                .addOnSuccessListener(documentReference -> {

                })
                .addOnFailureListener(e -> {

                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            messageListener.remove();
        }
    }
}