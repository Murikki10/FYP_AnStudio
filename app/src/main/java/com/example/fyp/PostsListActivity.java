package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsListActivity extends AppCompatActivity {

    private RecyclerView postsRecyclerView;
    private PostsAdapter postsAdapter;
    private List<Post> postList;
    private static final String TAG = "PostsListActivity";

    private int boardId; // 保存傳遞過來的 boardId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        // 初始化 RecyclerView
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();

        // 初始化適配器
        postsAdapter = new PostsAdapter(postList, post -> {
            Log.d(TAG, "Post clicked: " + post.getPostId());
            String authToken = AuthManager.getAuthToken(); // 獲取 Token
            if (authToken == null) {
                Toast.makeText(this, "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Passing authToken to PostDetailsActivity: " + authToken);
            Intent intent = new Intent(PostsListActivity.this, PostDetailsActivity.class);
            intent.putExtra("postId", post.getPostId());
            intent.putExtra("auth", "Bearer " + authToken);
            startActivity(intent);
        });
        postsRecyclerView.setAdapter(postsAdapter);

        // 設置 "Create Post" 按鈕點擊事件
        Button createPostButton = findViewById(R.id.createPostButton);
        createPostButton.setOnClickListener(v -> {
            Log.d(TAG, "Create Post button clicked");
            Intent intent = new Intent(PostsListActivity.this, PostFormActivity.class);
            startActivity(intent);
        });

        // 接收傳遞過來的 boardId
        boardId = getIntent().getIntExtra("boardId", -1);
        if (boardId == -1) {
            Toast.makeText(this, "Invalid board selected.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加載帖子
        fetchPostsFromApi(boardId, 1, 10);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity resumed, refreshing posts...");
        // 每次進入頁面時刷新帖子列表
        fetchPostsFromApi(boardId, 1, 10);
    }

    private void fetchPostsFromApi(int boardId, int page, int limit) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getPosts(boardId, page, limit).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body().getPosts());
                    postsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PostsListActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to load posts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Error loading posts: " + t.getMessage());
                Toast.makeText(PostsListActivity.this, "Error loading posts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}