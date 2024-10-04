package com.example.roommade;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private String currentUserId;
    private String[] nicknames = {
            "사랑스러운 고양이",
            "하품하는 돌고래",
            "슬픈 코끼리",
            "환호하는 코알라",
            "앉아있는 원숭이",
            "요리하는 얼룩말",
            "감시하는 강아지"
    };

    private Map<String, String> userNicknames = new HashMap<>();
    private Random random = new Random();

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        TextView textViewTimestamp;
        TextView textViewSenderName;

        public MessageViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            textViewSenderName = itemView.findViewById(R.id.textViewSenderName);
        }

        public void bind(Message message) {
            textViewMessage.setText(message.getMessage());

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String formattedTimestamp = dateFormat.format(new Date(message.getTimestamp()));
            textViewTimestamp.setText(formattedTimestamp);

            String senderId = message.getSenderId();
            if (senderId.equals(currentUserId)) {
                textViewSenderName.setText("작성자");
            } else {
                if (!userNicknames.containsKey(senderId)) {
                    String randomNickname = nicknames[random.nextInt(nicknames.length)];
                    userNicknames.put(senderId, randomNickname);
                }
                textViewSenderName.setText(userNicknames.get(senderId));
            }

            LinearLayout.LayoutParams messageParams = (LinearLayout.LayoutParams) textViewMessage.getLayoutParams();
            LinearLayout.LayoutParams timestampParams = (LinearLayout.LayoutParams) textViewTimestamp.getLayoutParams();
            LinearLayout.LayoutParams senderNameParams = (LinearLayout.LayoutParams) textViewSenderName.getLayoutParams(); // 추가된 부분

            if (message.getSenderId().equals(currentUserId)) {
                textViewMessage.setBackgroundResource(R.drawable.bubble_outgoing);
                textViewMessage.setTextColor(Color.WHITE);
                messageParams.gravity = Gravity.END;
                timestampParams.gravity = Gravity.END;
                senderNameParams.gravity = Gravity.END;
            } else {
                textViewMessage.setBackgroundResource(R.drawable.bubble_incoming);
                textViewMessage.setTextColor(Color.BLACK);
                messageParams.gravity = Gravity.START;
                timestampParams.gravity = Gravity.START;
                senderNameParams.gravity = Gravity.START;
            }

            textViewMessage.setLayoutParams(messageParams);
            textViewTimestamp.setLayoutParams(timestampParams);
            textViewSenderName.setLayoutParams(senderNameParams);
        }

    }
}