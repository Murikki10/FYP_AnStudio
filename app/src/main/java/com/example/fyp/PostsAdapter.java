package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> posts; // 帖子數據列表
    private OnItemClickListener onItemClickListener; // 點擊事件接口

    // 點擊事件接口
    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    // 構造函數
    public PostsAdapter(List<Post> posts, OnItemClickListener onItemClickListener) {
        this.posts = posts;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        // 綁定數據到 ViewHolder
        holder.titleTextView.setText(post.getTitle());
        holder.contentTextView.setText(post.getContent());
        holder.likeCountTextView.setText(String.format("%d Likes", post.getLikeCount()));
        holder.commentCountTextView.setText(String.format("%d Comments", post.getCommentCount()));
        holder.viewCountTextView.setText(String.format("%d Views", post.getViewCount()));

        // 設置點擊事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // ViewHolder 類
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, likeCountTextView, commentCountTextView, viewCountTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            viewCountTextView = itemView.findViewById(R.id.viewCountTextView);
        }
    }
}