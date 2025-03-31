package com.example.fyp;

import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        // 初始化 RecyclerView
        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(postList, post -> {
            // 點擊帖子時跳轉到詳情頁
            Log.d(TAG, "Post clicked: " + post.getPostId());
        });
        postsRecyclerView.setAdapter(postsAdapter);

        // 接收傳遞過來的 boardId
        int boardId = getIntent().getIntExtra("boardId", -1);
        if (boardId == -1) {
            Toast.makeText(this, "Invalid board selected.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 調用 API 獲取帖子
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