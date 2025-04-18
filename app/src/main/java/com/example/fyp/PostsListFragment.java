package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsListFragment extends Fragment {

    private static final String TAG = "PostsListFragment";

    private RecyclerView postsRecyclerView;
    private PostsAdapter postsAdapter;
    private List<Post> postList;
    private List<Board> boardList; // 保存所有分區數據
    private int selectedBoardId = -1; // 默認為 "ALL" 分區
    private int currentPage = 1; // 當前頁碼
    private boolean isLoading = false; // 是否正在加載數據

    private LinearLayout boardButtonContainer; // Board 按鈕區域
    private ProgressBar progressBar; // 加載進度條
    private Button loadMoreButton; // 加載更多按鈕

    public PostsListFragment() {
        // 空構造函數
    }
    @Override
    public void onResume() {
        super.onResume();
        // 重置頁碼並重新加載數據
        currentPage = 1;
        postList.clear();
        fetchPosts(currentPage);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts_list, container, false);

        // 初始化 RecyclerView
        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postsAdapter = new PostsAdapter(postList, post -> {
            Log.d(TAG, "Post clicked: " + post.getPostId());
            String authToken = AuthManager.getAuthToken();
            if (authToken == null) {
                Toast.makeText(requireContext(), "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 跳轉到帖子詳情 Fragment
            PostDetailsFragment fragment = new PostDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("postId", post.getPostId());
            bundle.putString("auth", "Bearer " + authToken);
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        postsRecyclerView.setAdapter(postsAdapter);

        // 初始化進度條
        progressBar = view.findViewById(R.id.progressBar);

        // 初始化 Board 按鈕區域
        boardButtonContainer = view.findViewById(R.id.boardButtonContainer);

        // 動態加載 Board 按鈕
        fetchBoards();

        // 初始化浮動按鈕
        FloatingActionButton fabCreatePost = view.findViewById(R.id.fab_create_post);
        fabCreatePost.setOnClickListener(v -> navigateToCreatePost());

        // 初始化「加載更多」按鈕
        loadMoreButton = view.findViewById(R.id.btn_load_more);
        loadMoreButton.setOnClickListener(v -> {
            if (!isLoading) {
                currentPage++;
                fetchPosts(currentPage);
            }
        });

        return view;
    }

    /**
     * 加載 Board 數據並顯示為按鈕
     */
    private void fetchBoards() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getBoards().enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(Call<List<Board>> call, Response<List<Board>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boardList = response.body();

                    // 添加 "ALL" 分區
                    Board allBoard = new Board();
                    allBoard.setBoardId(-1); // 虛擬 ID，表示 "ALL"
                    allBoard.setBoardName("ALL");
                    boardList.add(0, allBoard); // 將 "ALL" 分區添加到列表的第一個位置

                    setupBoardButtons(boardList); // 初始化 Board 按鈕
                } else {
                    Toast.makeText(requireContext(), "Failed to load boards", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Board>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error loading boards", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading boards: " + t.getMessage());
            }
        });
    }

    /**
     * 初始化 Board 按鈕
     */
    private void setupBoardButtons(List<Board> boards) {
        boardButtonContainer.removeAllViews(); // 清空舊按鈕

        for (Board board : boards) {
            Button button = new Button(requireContext());
            button.setText(board.getBoardName()); // 顯示分區名稱

            // 點擊事件：加載對應分區的帖子
            button.setOnClickListener(v -> {
                selectedBoardId = board.getBoardId();
                requireActivity().setTitle(board.getBoardName()); // 更新 Toolbar 標題
                currentPage = 1; // 重置頁碼
                postList.clear(); // 清空帖子列表
                fetchPosts(currentPage); // 加載該分區的帖子
            });

            boardButtonContainer.addView(button); // 添加按鈕到界面
        }

        // 默認選中 "ALL" 分區
        if (!boards.isEmpty()) {
            selectedBoardId = boards.get(0).getBoardId(); // 第一個分區的 ID，應為 "ALL"
            requireActivity().setTitle(boards.get(0).getBoardName()); // 更新 Toolbar 標題
            fetchPosts(currentPage); // 默認加載 "ALL" 分區的帖子
        }
    }

    /**
     * 加載指定頁面的帖子
     */
    private void fetchPosts(int page) {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiResponse> call;
        if (selectedBoardId == -1) {
            // 如果是 "ALL" 分區，不傳遞 boardId
            call = apiService.getPosts(null, page, 10);
        } else {
            // 傳遞具體的 boardId
            call = apiService.getPosts(selectedBoardId, page, 10);
        }

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Post> newPosts = response.body().getPosts();
                    if (newPosts.isEmpty() && page > 1) {
                        // 如果當前頁碼超過可用頁碼，顯示提示
                        Toast.makeText(requireContext(), "No more posts to load", Toast.LENGTH_SHORT).show();
                    } else if (page == 1) {
                        // 如果是加載第一頁，清空舊數據
                        postList.clear();
                        postList.addAll(newPosts);
                        postsAdapter.notifyDataSetChanged();
                    } else {
                        // 追加新數據
                        postList.addAll(newPosts);
                        postsAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error loading posts", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading posts: " + t.getMessage());
            }
        });
    }

    /**
     * 跳轉到創建帖子頁面
     */
    private void navigateToCreatePost() {
        Intent intent = new Intent(requireContext(), PostFormActivity.class);
        startActivity(intent);
    }
}