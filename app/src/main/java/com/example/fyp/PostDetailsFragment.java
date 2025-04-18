package com.example.fyp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailsFragment extends Fragment {

    private static final String TAG = "PostDetailsFragment";

    private int postId;
    private String auth;

    // UI 元素
    private TextView postTitle, postMetadata, postContent, likeCountTextView, commentCountTextView;
    private RecyclerView postImagesRecyclerView, commentsRecyclerView;
    private EditText commentInput;
    private ImageButton likeButton, commentButton;

    // 適配器和數據
    private PostImagesAdapter imagesAdapter;
    private CommentsAdapter commentsAdapter;
    private List<String> imageUrls = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();

    // 狀態數據
    private boolean isLiked = false;
    private int likeCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_post_details, container, false);

        // 初始化 UI 元素
        postTitle = view.findViewById(R.id.postTitle);
        postMetadata = view.findViewById(R.id.postMetadata);
        postContent = view.findViewById(R.id.postContent);
        likeCountTextView = view.findViewById(R.id.likeCount);
        commentCountTextView = view.findViewById(R.id.commentCount);
        postImagesRecyclerView = view.findViewById(R.id.postImagesRecyclerView); // 多張圖片的 RecyclerView
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView);
        commentInput = view.findViewById(R.id.commentInput);
        likeButton = view.findViewById(R.id.likeButton);
        commentButton = view.findViewById(R.id.commentButton);

        // 接收傳遞的 postId 和 authToken
        if (getArguments() != null) {
            postId = getArguments().getInt("postId", -1);
            auth = getArguments().getString("auth", null);
        }

        if (postId == -1 || auth == null || !auth.startsWith("Bearer ")) {
            Toast.makeText(requireContext(), "Invalid post ID or missing token.", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return view;
        }

        Log.d(TAG, "Received postId: " + postId);
        Log.d(TAG, "Received auth token: " + auth);

        // 初始化 RecyclerView
        setupRecyclerViews();

        // 加載帖子詳情和評論
        fetchPostDetails();
        fetchComments();

        // 點擊點贊按鈕
        likeButton.setOnClickListener(v -> toggleLike());

        // 點擊評論按鈕
        commentButton.setOnClickListener(v -> addComment());

        return view;
    }

    private void setupRecyclerViews() {
        // 設置圖片 RecyclerView
        imagesAdapter = new PostImagesAdapter(imageUrls);
        postImagesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        postImagesRecyclerView.setAdapter(imagesAdapter);

        // 設置評論 RecyclerView
        commentsAdapter = new CommentsAdapter(commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    private void fetchPostDetails() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getPostDetails(auth, postId).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Post post = response.body();
                    Log.d(TAG, "Post details loaded: " + post.toString());

                    // 更新 UI
                    postTitle.setText(post.getTitle());
                    String metadata = "By " + post.getAuthorName() + " on " + post.getCreatedAt();
                    postMetadata.setText(metadata);
                    postContent.setText(post.getContent());

                    // 加載圖片
                    String imageUrl = post.getImageUrl();
                    imageUrls.clear(); // 清空舊的圖片列表

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // 如果只有一張圖片，將其加入列表
                        imageUrls.add(imageUrl);
                    }

                    // 更新 RecyclerView
                    imagesAdapter.notifyDataSetChanged();

                    // 如果沒有圖片，隱藏 RecyclerView
                    if (imageUrls.isEmpty()) {
                        postImagesRecyclerView.setVisibility(View.GONE);
                    } else {
                        postImagesRecyclerView.setVisibility(View.VISIBLE);
                    }

                    // 更新點贊數
                    likeCount = post.getLikeCount();
                    updateLikeUI();

                    // 獲取點贊狀態
                    fetchLikeStatus();
                } else if (response.code() == 404) {
                    Toast.makeText(requireContext(), "Post not found.", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed(); // 返回上一個頁面
                } else {
                    Toast.makeText(requireContext(), "Failed to load post details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e(TAG, "Error loading post details: " + t.getMessage());
                Toast.makeText(requireContext(), "Error loading post details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchComments() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getComments(auth, postId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentList.clear();
                    commentList.addAll(response.body());
                    commentsAdapter.notifyDataSetChanged();

                    // 更新評論數量
                    int commentCount = commentList.size();
                    commentCountTextView.setText(String.valueOf(commentCount));
                } else {
                    Toast.makeText(requireContext(), "Failed to load comments", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to load comments: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e(TAG, "Error loading comments: " + t.getMessage());
                Toast.makeText(requireContext(), "Error loading comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLikeStatus() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.isLiked(auth, postId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseBody = response.body();
                    isLiked = responseBody.get("isLiked").getAsBoolean(); // 從返回值解析點贊狀態
                    updateLikeUI();
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch like status", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to fetch like status: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Error fetching like status: " + t.getMessage());
                Toast.makeText(requireContext(), "Error fetching like status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleLike() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.toggleLike(auth, postId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 切換點贊狀態
                    isLiked = !isLiked;
                    likeCount += isLiked ? 1 : -1;
                    updateLikeUI();
                } else {
                    Toast.makeText(requireContext(), "Failed to toggle like", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Error toggling like", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error toggling like: " + t.getMessage());
            }
        });
    }

    private void addComment() {
        String content = commentInput.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.addComment(auth, postId, new CommentRequest(content)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    commentInput.setText("");
                    fetchComments();
                } else {
                    Toast.makeText(requireContext(), "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Error adding comment", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error adding comment: " + t.getMessage());
            }
        });
    }

    private void updateLikeUI() {
        likeCountTextView.setText(String.valueOf(likeCount));
        likeButton.setImageResource(isLiked ? R.drawable.ic_like_active : R.drawable.ic_like_inactive);
    }
}