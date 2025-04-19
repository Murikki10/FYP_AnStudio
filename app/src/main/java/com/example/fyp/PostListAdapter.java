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

public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_POST = 1; // 普通帖子列表項目類型
    private static final int VIEW_TYPE_LOADING = 2; // 加載中列表項目類型

    private List<Post> posts; // 帖子數據列表
    private OnItemClickListener onItemClickListener; // 點擊事件接口
    private boolean isLoadingAdded = false; // 是否已添加 "加載中" 項目

    // 點擊事件接口
    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    // 構造函數
    public PostListAdapter(List<Post> posts, OnItemClickListener onItemClickListener) {
        this.posts = posts;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        // 如果當前項目是 "加載中"，返回 VIEW_TYPE_LOADING
        return posts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_POST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_POST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
            return new PostViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_POST) {
            Post post = posts.get(position);
            PostViewHolder postViewHolder = (PostViewHolder) holder;

            // 綁定帖子標題與內容
            postViewHolder.titleTextView.setText(post.getTitle());
            postViewHolder.contentTextView.setText(post.getContent());

            // 綁定 Like、Comment 和 View 數量
            postViewHolder.likeCountTextView.setText(String.valueOf(post.getLikeCount()));
            postViewHolder.commentCountTextView.setText(String.valueOf(post.getCommentCount()));
            postViewHolder.viewCountTextView.setText(String.valueOf(post.getViewCount()));

            // 加載圖片（如果有）
            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                postViewHolder.postImageView.setVisibility(View.VISIBLE);
                Glide.with(postViewHolder.itemView.getContext())
                        .load(post.getImageUrl())
                        .into(postViewHolder.postImageView);
            } else {
                postViewHolder.postImageView.setVisibility(View.GONE);
            }

            // 點擊事件：跳轉到詳情頁
            postViewHolder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(post);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // 添加 "加載中" 項目
    public void addLoadingItem() {
        if (!isLoadingAdded) {
            posts.add(null); // 插入一個空項目作為 "加載中" 標記
            isLoadingAdded = true;
            notifyItemInserted(posts.size() - 1);
        }
    }

    // 移除 "加載中" 項目
    public void removeLoadingItem() {
        if (isLoadingAdded && posts.size() > 0) {
            int position = posts.size() - 1;
            if (posts.get(position) == null) {
                posts.remove(position);
                notifyItemRemoved(position);
                isLoadingAdded = false;
            }
        }
    }

    // ViewHolder 類 - 普通帖子
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

    // ViewHolder 類 - 加載中
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}