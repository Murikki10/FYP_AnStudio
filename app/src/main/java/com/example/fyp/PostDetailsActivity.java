package com.example.fyp;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailsActivity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // 初始化 UI 元素
        postTitle = findViewById(R.id.postTitle);
        postMetadata = findViewById(R.id.postMetadata);
        postContent = findViewById(R.id.postContent);
        likeCountTextView = findViewById(R.id.likeCount);
        commentCountTextView = findViewById(R.id.commentCount); // 綁定留言數量的 TextView
        postImagesRecyclerView = findViewById(R.id.postImagesRecyclerView);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentInput = findViewById(R.id.commentInput);
        likeButton = findViewById(R.id.likeButton);
        commentButton = findViewById(R.id.commentButton);

        // 接收傳遞的 postId 和 authToken
        postId = getIntent().getIntExtra("postId", -1);
        auth = getIntent().getStringExtra("auth");

        if (postId == -1 || auth == null || !auth.startsWith("Bearer ")) {
            Toast.makeText(this, "Invalid post ID or missing auth token.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid postId or auth token. postId: " + postId + ", auth: " + auth);
            finish();
            return;
        }

        Log.d(TAG, "Received postId: " + postId);
        Log.d(TAG, "Received auth token: " + auth);

        // 初始化 RecyclerView
        setupRecyclerViews();

        // 加載帖子詳情和留言
        fetchPostDetails();
        fetchComments();

        // 點擊點贊按鈕
        likeButton.setOnClickListener(v -> toggleLike());

        // 點擊留言按鈕
        commentButton.setOnClickListener(v -> addComment());
    }

    private void setupRecyclerViews() {
        // 設置圖片 RecyclerView
        imagesAdapter = new PostImagesAdapter(imageUrls);
        postImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        postImagesRecyclerView.setAdapter(imagesAdapter);

        // 設置留言 RecyclerView
        commentsAdapter = new CommentsAdapter(commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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

                    // 更新圖片數據
                    imageUrls.clear();
                    imageUrls.addAll(post.getImageUrls());
                    imagesAdapter.notifyDataSetChanged();

                    // 更新點贊數
                    likeCount = post.getLikeCount();
                    updateLikeUI();

                    // 獲取點贊狀態
                    fetchLikeStatus();
                } else {
                    Toast.makeText(PostDetailsActivity.this, "Failed to load post details", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to load post details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e(TAG, "Error loading post details: " + t.getMessage());
                Toast.makeText(PostDetailsActivity.this, "Error loading post details", Toast.LENGTH_SHORT).show();
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

                    // 更新留言數量
                    int commentCount = commentList.size();
                    commentCountTextView.setText(commentCount + " Comments");
                } else {
                    Toast.makeText(PostDetailsActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to load comments: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.e(TAG, "Error loading comments: " + t.getMessage());
                Toast.makeText(PostDetailsActivity.this, "Error loading comments", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PostDetailsActivity.this, "Failed to fetch like status", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to fetch like status: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Error fetching like status: " + t.getMessage());
                Toast.makeText(PostDetailsActivity.this, "Error fetching like status", Toast.LENGTH_SHORT).show();
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

                    // 自動刷新帖子詳情和留言
                    refreshData();
                } else {
                    Toast.makeText(PostDetailsActivity.this, "Failed to toggle like", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error toggling like: " + t.getMessage());
                Toast.makeText(PostDetailsActivity.this, "Error toggling like", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addComment() {
        String content = commentInput.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        CommentRequest commentRequest = new CommentRequest(content);
        apiService.addComment(auth, postId, commentRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PostDetailsActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                    commentInput.setText("");

                    // 自動刷新帖子詳情和留言
                    refreshData();
                } else {
                    Toast.makeText(PostDetailsActivity.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error adding comment: " + t.getMessage());
                Toast.makeText(PostDetailsActivity.this, "Error adding comment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLikeUI() {
        // 更新點贊數量
        likeCountTextView.setText(likeCount + " Likes");

        // 更新按鈕圖片
        likeButton.setImageResource(isLiked ? R.drawable.ic_like_active : R.drawable.ic_like_inactive);
    }

    private void refreshData() {
        // 刷新帖子詳情和留言
        fetchPostDetails();
        fetchComments();
    }
}