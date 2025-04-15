package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final String currentUser; // 當前用戶名

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

        if (message.getUser().equals(currentUser)) {
            // 當前用戶發送的消息，顯示在右邊
            holder.rightMessageContainer.setVisibility(View.VISIBLE);
            holder.leftMessageContainer.setVisibility(View.GONE);
            holder.rightMessageContent.setText(message.getContent());
        } else {
            // 其他用戶發送的消息，顯示在左邊
            holder.leftMessageContainer.setVisibility(View.VISIBLE);
            holder.rightMessageContainer.setVisibility(View.GONE);
            holder.leftMessageContent.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftMessageContainer, rightMessageContainer;
        TextView leftMessageContent, rightMessageContent;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            leftMessageContainer = itemView.findViewById(R.id.leftMessageContainer);
            rightMessageContainer = itemView.findViewById(R.id.rightMessageContainer);
            leftMessageContent = itemView.findViewById(R.id.leftMessageContent);
            rightMessageContent = itemView.findViewById(R.id.rightMessageContent);
        }
    }
}