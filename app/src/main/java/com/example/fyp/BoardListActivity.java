package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardListActivity extends AppCompatActivity {
    private RecyclerView boardRecyclerView;
    private BoardAdapter adapter;
    private List<Board> boardList = new ArrayList<>();
    private static final String TAG = "BoardListActivity";

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_list);

        // 初始化 RecyclerView
        boardRecyclerView = findViewById(R.id.boardRecyclerView);
        boardRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化 ProgressBar
        progressBar = findViewById(R.id.progressBar);

        // 初始化適配器
        adapter = new BoardAdapter(boardList, new BoardAdapter.OnBoardClickListener() {
            @Override
            public void onFollowClick(int boardId, boolean follow) {
                Log.d(TAG, "Follow button clicked for boardId: " + boardId + ", follow: " + follow);
                updateFollowStatus(boardId, follow);
            }

            @Override
            public void onBoardClick(int boardId) {
                Log.d(TAG, "Board clicked with boardId: " + boardId);
                // 傳遞 boardId 到帖子列表頁面
                Intent intent = new Intent(BoardListActivity.this, PostsListFragment.class);
                intent.putExtra("boardId", boardId); // 傳遞 boardId
                startActivity(intent);
            }
        });

        boardRecyclerView.setAdapter(adapter);

        // 加載 Board 數據
        fetchBoards();
    }

    // 從後端獲取 Board 列表
    private void fetchBoards() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        progressBar.setVisibility(View.VISIBLE);

        apiService.getBoards().enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(@NonNull Call<List<Board>> call, @NonNull Response<List<Board>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    boardList.clear();
                    boardList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(BoardListActivity.this, "Failed to fetch boards", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Board>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BoardListActivity.this, "Failed to fetch boards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 更新 Follow 狀態，保持不變
    private void updateFollowStatus(int boardId, boolean follow) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        for (Board board : boardList) {
            if (board.getBoardId() == boardId) {
                board.setFollowed(follow);
                break;
            }
        }
        adapter.notifyDataSetChanged();

        apiService.updateFollowStatus(boardId, new FollowRequest(follow)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(BoardListActivity.this, "Failed to update follow status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(BoardListActivity.this, "Failed to update follow status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}