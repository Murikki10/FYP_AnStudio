package com.example.fyp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFormFragment extends Fragment {

    private static final String TAG = "PostFormFragment";

    private Spinner boardSpinner;
    private Button postButton;
    private RichEditor richEditor;
    private ChipGroup tagChipGroup;
    private ImageView backButton;
    private List<Tag> tagOptions = new ArrayList<>();
    private List<Integer> selectedTagIds = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_post_form, container, false);

        // 初始化 UI 元素
        boardSpinner = view.findViewById(R.id.board_spinner);
        postButton = view.findViewById(R.id.post_button);
        richEditor = view.findViewById(R.id.rich_editor);
        tagChipGroup = view.findViewById(R.id.tag_chip_group);
        backButton = view.findViewById(R.id.back_button);

        // 初始化富文本编辑器
        richEditor.setEditorHeight(200);
        richEditor.setEditorFontSize(16);
        richEditor.setPadding(10, 10, 10, 10);
        richEditor.setPlaceholder("Write something...");

        // 加载分区和标签
        fetchBoards();
        fetchTags();

        // 发布按钮点击事件
        postButton.setOnClickListener(v -> createPost());

        // 返回按钮点击事件
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void fetchBoards() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getBoards().enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(@NonNull Call<List<Board>> call, @NonNull Response<List<Board>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupBoardSpinner(response.body());
                } else {
                    Toast.makeText(requireContext(), "Failed to load boards", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Board>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error loading boards", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading boards: " + t.getMessage());
            }
        });
    }

    private void setupBoardSpinner(List<Board> boards) {
        // Spinner 适配器设置逻辑
        // （用 Android 的 ArrayAdapter 或自定义适配器实现）
    }

    private void fetchTags() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getTags().enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tag>> call, @NonNull Response<List<Tag>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tagOptions = response.body();
                    setupTagChips();
                } else {
                    Toast.makeText(requireContext(), "Failed to load tags", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tag>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error loading tags", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading tags: " + t.getMessage());
            }
        });
    }

    private void setupTagChips() {
        tagChipGroup.removeAllViews();

        for (Tag tag : tagOptions) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag.getTagName());
            chip.setCheckable(true);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTagIds.add(tag.getTagId());
                } else {
                    selectedTagIds.remove(Integer.valueOf(tag.getTagId()));
                }
            });

            tagChipGroup.addView(chip);
        }
    }

    private void createPost() {
        Board selectedBoard = (Board) boardSpinner.getSelectedItem();
        String title = "Sample Title"; // 使用实际的标题输入框值
        String content = richEditor.getHtml();

        if (selectedBoard == null || title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        PostRequest postRequest = new PostRequest(
                selectedBoard.getBoardId(),
                title,
                content,
                selectedTagIds,
                123 // 用户 ID 示例
        );

        postButton.setEnabled(false);
        postButton.setText("Posting...");

        apiService.createPost(postRequest).enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostsResponse> call, @NonNull Response<PostsResponse> response) {
                postButton.setEnabled(true);
                postButton.setText("Post");
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostsResponse> call, @NonNull Throwable t) {
                postButton.setEnabled(true);
                postButton.setText("Post");
                Toast.makeText(requireContext(), "Error creating post", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error creating post: " + t.getMessage());
            }
        });
    }
}