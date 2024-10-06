package com.example.roommade;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private String currentUserId;
    private String postUserId;
    private List<String> participantIds;
    private String[] nicknames = {
            "사랑스러운 고양이",
            "하품하는 돌고래",
            "슬픈 코끼리",
            "환호하는 코알라",
            "앉아 있는 원숭이",
            "요리하는 얼룩말",
            "감시하는 강아지",
            "웃고 있는 팬더",
            "춤추는 토끼",
            "밥 먹는 햄스터",
            "개굴개굴 개구리",
            "자고 있는 하마",
            "미소 짓는 펭귄",
            "아주 빠른 거북이",
            "여행하는 여우",
            "책 읽는 다람쥐",
            "걷고 있는 알파카",
            "소리치는 사슴",
            "손 씻는 늑대"
    };

    private Map<String, String> userNicknames = new HashMap<>();
    private Random random = new Random();
    private static final String PREFS_NAME = "NicknamesPrefs";
    private static final String NICKNAME_KEY_PREFIX = "nickname_";
    private Context context;

    public MessageAdapter(Context context, List<Message> messages, String currentUserId, String postUserId, List<String> participantIds) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.postUserId = postUserId;
        this.participantIds = participantIds;

        assignNicknames();
    }

    private void assignNicknames() {
        if (participantIds == null) {
            participantIds = new ArrayList<>();
        }

        for (String participantId : participantIds) {
            String nickname = getNicknameFromPrefs(participantId);
            if (nickname == null) {
                nickname = assignRandomNickname(participantId);
            }
            userNicknames.put(participantId, nickname);
        }
    }


    private String assignRandomNickname(String participantId) {
        List<String> availableNicknames = new ArrayList<>();
        for (String nickname : nicknames) {
            if (!userNicknames.containsValue(nickname)) {
                availableNicknames.add(nickname);
            }
        }

        if (!availableNicknames.isEmpty()) {
            String randomNickname = availableNicknames.remove(random.nextInt(availableNicknames.size()));
            saveNicknameToPrefs(participantId, randomNickname);
            return randomNickname;
        } else {
            return "알 수 없는 사용자";
        }
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

            if (senderId.equals(postUserId)) {
                textViewSenderName.setText("작성자");
            } else if (participantIds.contains(senderId)) {
                String nickname = userNicknames.get(senderId);
                if (nickname == null) {
                    textViewSenderName.setText("알 수 없는 사용자");
                } else {
                    textViewSenderName.setText(nickname);
                }
            } else {
                textViewSenderName.setText("알 수 없는 사용자");
            }

            LinearLayout.LayoutParams messageParams = (LinearLayout.LayoutParams) textViewMessage.getLayoutParams();
            LinearLayout.LayoutParams timestampParams = (LinearLayout.LayoutParams) textViewTimestamp.getLayoutParams();
            LinearLayout.LayoutParams senderNameParams = (LinearLayout.LayoutParams) textViewSenderName.getLayoutParams();

            if (senderId.equals(currentUserId)) {
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

    private String getNicknameFromPrefs(String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(NICKNAME_KEY_PREFIX + userId, null);
    }

    private void saveNicknameToPrefs(String userId, String nickname) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NICKNAME_KEY_PREFIX + userId, nickname);
        editor.apply();
    }
}
