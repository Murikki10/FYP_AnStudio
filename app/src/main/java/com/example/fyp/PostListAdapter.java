package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    private List<Post> posts; // 帖子數據列表
    private OnItemClickListener onItemClickListener; // 點擊事件接口

    // 點擊事件接口
    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    // 構造函數
    public PostListAdapter(List<Post> posts, OnItemClickListener onItemClickListener) {
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

        // 綁定帖子標題與內容
        holder.titleTextView.setText(post.getTitle());
        holder.contentTextView.setText(post.getContent());

        // 綁定 Like、Comment 和 View 數量
        holder.likeCountTextView.setText(String.valueOf(post.getLikeCount()));
        holder.commentCountTextView.setText(String.valueOf(post.getCommentCount()));
        holder.viewCountTextView.setText(String.valueOf(post.getViewCount()));

        // 加載圖片（如果有）
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.postImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(post.getImageUrl())
                    .into(holder.postImageView);
        } else {
            holder.postImageView.setVisibility(View.GONE);
        }

        // 點擊事件：跳轉到詳情頁
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
        ImageView postImageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            likeCountTextView = itemView.findViewById(R.id.likeCountTextView);
            commentCountTextView = itemView.findViewById(R.id.commentCountTextView);
            viewCountTextView = itemView.findViewById(R.id.viewCountTextView);
            postImageView = itemView.findViewById(R.id.postImageView);
        }
    }
}