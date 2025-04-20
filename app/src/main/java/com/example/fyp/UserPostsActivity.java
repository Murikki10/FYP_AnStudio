package com.example.fyp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPostsActivity extends AppCompatActivity {

    private RecyclerView postsRecyclerView;
    private GenericAdapter<Post> genericAdapter; // 使用通用適配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        // 初始化 RecyclerView
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化通用適配器
        genericAdapter = new GenericAdapter<>(
                null, // 初始數據為空，稍後更新
                (textView, post) -> {
                    // 綁定數據到視圖
                    if (textView.getId() == R.id.titleTextView) {
                        textView.setText(post.getTitle());
                    } else if (textView.getId() == R.id.contentTextView) {
                        textView.setText(post.getContent());
                    } else if (textView.getId() == R.id.likeCountTextView) {
                        textView.setText(post.getLikeCount() + " Likes");
                    } else if (textView.getId() == R.id.commentCountTextView) {
                        textView.setText(post.getCommentCount() + " Comments");
                    } else if (textView.getId() == R.id.viewCountTextView) {
                        textView.setText(post.getViewCount() + " Views");
                    }
                }
        );

        // 設置適配器
        postsRecyclerView.setAdapter(genericAdapter);

        // 加載用戶帖子
        loadUserPosts();
    }

    private void loadUserPosts() {
        String token = AuthManager.getAuthToken();
        Log.d("UserPostsActivity", "Fetched Token: " + token);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserPostsResponse> call = apiService.getUserPosts("Bearer " + token);
        call.enqueue(new Callback<UserPostsResponse>() {
            @Override
            public void onResponse(Call<UserPostsResponse> call, Response<UserPostsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Post> posts = response.body().getData();
                    Log.d("UserPostsActivity", "Fetched Posts: " + posts);
                    genericAdapter.updateItems(posts); // 更新適配器數據
                } else {
                    Log.e("UserPostsActivity", "Failed to load posts: " + response.toString());
                    Toast.makeText(UserPostsActivity.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserPostsResponse> call, Throwable t) {
                Log.e("UserPostsActivity", "Error fetching posts", t);
                Toast.makeText(UserPostsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}