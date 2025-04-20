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
    private List<Post> postList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    private List<Board> boardList = new ArrayList<>();
    private int selectedBoardId = -1; // 默認為 "ALL" 分區
    private int currentPage = 1;
    private boolean isLoading = false;

    private LinearLayout boardButtonContainer;
    private ProgressBar progressBar;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    public PostsListFragment() {
        // 空構造函數
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts_list, container, false);

        // 初始化 UI 元素
        initViews(view);

        // 設置 RecyclerView 和適配器
        setupRecyclerView();

        // 加載 Board 按鈕
        fetchBoards();

        // 設置刷新邏輯
        setupSwipeRefreshListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 當返回到此 Fragment 時自動刷新列表
        if (!isLoading) {
            Log.d(TAG, "Resuming and refreshing posts...");
            currentPage = 1;
            postList.clear();
            fetchPosts(currentPage);
        }
    }

    /**
     * 初始化視圖
     */
    private void initViews(View view) {
        postsRecyclerView = view.findViewById(R.id.postsRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        boardButtonContainer = view.findViewById(R.id.boardButtonContainer);

        FloatingActionButton fabCreatePost = view.findViewById(R.id.fab_create_post);
        fabCreatePost.setOnClickListener(v -> navigateToCreatePost());
    }

    /**
     * 設置 RecyclerView
     */
    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        postsRecyclerView.setLayoutManager(layoutManager);

        postsAdapter = new PostsAdapter(postList, post -> {
            Log.d(TAG, "Post clicked: " + post.getPostId());

            // 確保用戶已登入
            String authToken = AuthManager.getAuthToken();
            if (authToken == null) {
                Toast.makeText(requireContext(), "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 跳轉到帖子詳情頁
            navigateToPostDetails(post.getPostId(), authToken);
        });

        postsRecyclerView.setAdapter(postsAdapter);

        // 添加滾動監聽器
        postsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 如果滾動到列表底部，則加載下一頁
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    currentPage++;
                    fetchPosts(currentPage);
                }
            }
        });
    }

    /**
     * 設置下拉刷新邏輯
     */
    private void setupSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 检查 RecyclerView 是否滚动到顶部
            if (postsRecyclerView.canScrollVertically(-1)) {
                // 如果不能滚动到顶部，取消刷新
                swipeRefreshLayout.setRefreshing(false);
                return;
            }

            // 如果在顶部，执行刷新逻辑
            currentPage = 1;
            postList.clear();
            fetchPosts(currentPage);
        });

        // 当滑动时动态禁用或启用 SwipeRefreshLayout
        postsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // 如果向下滚动，禁用刷新；如果在顶部，启用刷新
                swipeRefreshLayout.setEnabled(!postsRecyclerView.canScrollVertically(-1));
            }
        });
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
                    Board allBoard = new Board(-1, "ALL");
                    boardList.add(0, allBoard);

                    setupBoardButtons();
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
    private void setupBoardButtons() {
        boardButtonContainer.removeAllViews();

        for (Board board : boardList) {
            Button button = new Button(requireContext());
            button.setText(board.getBoardName());

            button.setOnClickListener(v -> {
                selectedBoardId = board.getBoardId();
                requireActivity().setTitle(board.getBoardName());
                currentPage = 1;
                postList.clear();
                fetchPosts(currentPage);
            });

            boardButtonContainer.addView(button);
        }

        // 默認加載第一個分區
        if (!boardList.isEmpty()) {
            selectedBoardId = boardList.get(0).getBoardId();
            requireActivity().setTitle(boardList.get(0).getBoardName());
            fetchPosts(currentPage);
        }
    }

    /**
     * 加載帖子數據
     */
    private void fetchPosts(int page) {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ApiResponse> call = (selectedBoardId == -1)
                ? apiService.getPosts(null, page, 10)
                : apiService.getPosts(selectedBoardId, page, 10);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Post> newPosts = response.body().getPosts();
                    if (page == 1) {
                        postList.clear();
                    }
                    postList.addAll(newPosts);
                    postsAdapter.notifyDataSetChanged();

                    if (newPosts.size() < 10) {
                        Toast.makeText(requireContext(), "All Post Has Been Load", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error loading posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 跳轉到帖子詳情頁
     */
    private void navigateToPostDetails(int postId, String authToken) {
        PostDetailsFragment fragment = new PostDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("postId", postId);
        bundle.putString("auth", "Bearer " + authToken);
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * 跳轉到創建帖子頁
     */
    private void navigateToCreatePost() {
        Intent intent = new Intent(requireContext(), PostFormActivity.class);
        startActivity(intent);
    }
}