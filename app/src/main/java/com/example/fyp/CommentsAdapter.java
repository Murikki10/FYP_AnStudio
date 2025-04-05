package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> commentList; // 留言列表

    // Constructor
    public CommentsAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the comment item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        // Bind data to the views
        Comment comment = commentList.get(position);
        holder.authorTextView.setText(comment.getAuthorName()); // 顯示留言用戶名稱
        holder.contentTextView.setText(comment.getContent()); // 顯示留言內容
        holder.dateTextView.setText(comment.getCreatedAt()); // 顯示留言時間
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    // ViewHolder class
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView authorTextView, contentTextView, dateTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.commentAuthor); // 留言用戶名稱
            contentTextView = itemView.findViewById(R.id.commentContent); // 留言內容
            dateTextView = itemView.findViewById(R.id.commentDate); // 留言時間
        }
    }
}