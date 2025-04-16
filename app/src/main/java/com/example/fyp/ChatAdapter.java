package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final String currentUser; // 當前用戶名
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ChatAdapter(List<ChatMessage> chatMessages, String currentUser) {
        this.chatMessages = chatMessages;
        this.currentUser = currentUser; // 傳入當前用戶名
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        // 獲取用戶名，默認為 Anonymous User
        String userName = message.getUser().isEmpty() ? "Anonymous User" : message.getUser();
        String messageTime = timeFormat.format(new Date()); // 當前消息的時間

        if (message.getUser().equals(currentUser)) {
            // 當前用戶發送的消息，顯示在右邊
            holder.rightMessageContainer.setVisibility(View.VISIBLE);
            holder.leftMessageContainer.setVisibility(View.GONE);

            holder.rightMessageContent.setText(message.getContent());
            holder.rightMessageUser.setText(userName);
            holder.rightMessageTime.setText(messageTime);
        } else {
            // 其他用戶發送的消息，顯示在左邊
            holder.leftMessageContainer.setVisibility(View.VISIBLE);
            holder.rightMessageContainer.setVisibility(View.GONE);

            holder.leftMessageContent.setText(message.getContent());
            holder.leftMessageUser.setText(userName);
            holder.leftMessageTime.setText(messageTime);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        View leftMessageContainer, rightMessageContainer;
        TextView leftMessageContent, rightMessageContent;
        TextView leftMessageUser, rightMessageUser;
        TextView leftMessageTime, rightMessageTime;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            leftMessageContainer = itemView.findViewById(R.id.leftMessageContainer);
            rightMessageContainer = itemView.findViewById(R.id.rightMessageContainer);
            leftMessageContent = itemView.findViewById(R.id.leftMessageContent);
            rightMessageContent = itemView.findViewById(R.id.rightMessageContent);
            leftMessageUser = itemView.findViewById(R.id.leftMessageUser);
            rightMessageUser = itemView.findViewById(R.id.rightMessageUser);
            leftMessageTime = itemView.findViewById(R.id.leftMessageTime);
            rightMessageTime = itemView.findViewById(R.id.rightMessageTime);
        }
    }
}