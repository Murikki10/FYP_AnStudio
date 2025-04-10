package com.example.fyp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostsListFragment extends Fragment {

    private static final String TAG = "PostsListFragment";

    private RecyclerView postsRecyclerView;
    private ChipGroup boardChipGroup;
    private PostsAdapter postsAdapter;
    private List<Post> postList;
    private List<Board> boardList; // 保存所有 board 数据
    private int selectedBoardId = -1; // 当前选中的 boardId

    public PostsListFragment() {
        // 空构造函数
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

            // 跳转到帖子详情 Fragment
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

        // 初始化 ChipGroup
        boardChipGroup = view.findViewById(R.id.boardChipGroup);

        // 初始化浮动按钮
        FloatingActionButton fabCreatePost = view.findViewById(R.id.fab_create_post);
        fabCreatePost.setOnClickListener(v -> navigateToCreatePost());

        // 加载 Board 和 Post 数据
        fetchBoards();

        return view;
    }

    /**
     * 加载 Board 数据并动态生成 Chips
     */
    private void fetchBoards() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getBoards().enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(Call<List<Board>> call, Response<List<Board>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boardList = response.body();
                    setupBoardChips(boardList); // 初始化 Chips
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
     * 初始化 Board Chips
     */
    private void setupBoardChips(List<Board> boards) {
        boardChipGroup.removeAllViews(); // 清空旧 Chip

        for (Board board : boards) {
            Chip chip = new Chip(requireContext());
            chip.setText(board.getBoardName());
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setChipBackgroundColorResource(R.color.chip_background_selector);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_selector, null));

            chip.setOnClickListener(v -> {
                selectedBoardId = board.getBoardId();
                fetchPosts(); // 加载选中分区的帖子
            });

            boardChipGroup.addView(chip);
        }

        // 默认选中第一个 Board
        if (!boards.isEmpty()) {
            selectedBoardId = boards.get(0).getBoardId();
            ((Chip) boardChipGroup.getChildAt(0)).setChecked(true);
            fetchPosts();
        }
    }

    /**
     * 加载选中 Board 的帖子
     */
    private void fetchPosts() {
        if (selectedBoardId == -1) {
            Log.e(TAG, "No board selected.");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getPosts(selectedBoardId, 1, 10).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body().getPosts());
                    postsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Error loading posts", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading posts: " + t.getMessage());
            }
        });
    }

    /**
     * 跳转到创建帖子页面
     */
    private void navigateToCreatePost() {
        PostFormFragment fragment = new PostFormFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}