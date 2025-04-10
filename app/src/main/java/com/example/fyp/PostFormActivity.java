package com.example.fyp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFormActivity extends AppCompatActivity {

    private static final String TAG = "PostFormActivity";

    private Spinner boardSpinner;
    private Button postButton;
    private RichEditor richEditor;
    private ChipGroup tagChipGroup;
    private List<Tag> tagOptions = new ArrayList<>(); // 從後端獲取的標籤
    private List<Integer> selectedTagIds = new ArrayList<>(); // 選中的標籤 ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_form);

        // 初始化 UI 元素
        boardSpinner = findViewById(R.id.board_spinner);
        postButton = findViewById(R.id.post_button);
        richEditor = findViewById(R.id.rich_editor);
        tagChipGroup = findViewById(R.id.tag_chip_group);

        // 初始化富文本編輯器
        richEditor.setEditorHeight(200);
        richEditor.setEditorFontSize(16);
        richEditor.setPadding(10, 10, 10, 10);
        richEditor.setPlaceholder("Write something...");

        // 加載分區和標籤
        fetchBoards(); // 加載分區
        fetchTags();   // 加載標籤

        // 發佈按鈕點擊事件
        postButton.setOnClickListener(v -> createPost());
    }

    // 加載分區數據
    private void fetchBoards() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getBoards().enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(@NonNull Call<List<Board>> call, @NonNull Response<List<Board>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupBoardSpinner(response.body()); // 初始化 Spinner 的數據
                    Log.d(TAG, "Boards loaded successfully.");
                } else {
                    Log.e(TAG, "Failed to load boards: " + response.errorBody());
                    Toast.makeText(PostFormActivity.this, "Failed to load boards", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Board>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error loading boards: " + t.getMessage());
                Toast.makeText(PostFormActivity.this, "Error loading boards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 初始化分區數據到 Spinner
    private void setupBoardSpinner(List<Board> boards) {
        ArrayAdapter<Board> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                boards
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardSpinner.setAdapter(adapter);
    }

    // 加載標籤數據
    private void fetchTags() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getTags().enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tag>> call, @NonNull Response<List<Tag>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tagOptions = response.body();
                    setupTagChips(); // 初始化標籤到 ChipGroup
                } else {
                    Log.e(TAG, "Failed to load tags: " + response.errorBody());
                    Toast.makeText(PostFormActivity.this, "Failed to load tags", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tag>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error loading tags: " + t.getMessage());
                Toast.makeText(PostFormActivity.this, "Error loading tags", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 初始化標籤到 ChipGroup
    private void setupTagChips() {
        tagChipGroup.removeAllViews();

        for (Tag tag : tagOptions) {
            Chip chip = new Chip(this);
            chip.setText(tag.getTagName());
            chip.setCheckable(true);

            // 選中或取消標籤的邏輯
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

    // 發佈貼文
    private void createPost() {
        Board selectedBoard = (Board) boardSpinner.getSelectedItem();
        String title = ((EditText) findViewById(R.id.title_input)).getText().toString().trim();
        String content = richEditor.getHtml();

        // 假設 userId 來自於本地存儲或 Session
        int userId = 123; // 替換成實際的 userId

        if (selectedBoard == null || title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        PostRequest postRequest = new PostRequest(
                selectedBoard.getBoardId(),
                title,
                content,
                selectedTagIds,
                userId // 傳遞 userId
        );

        postButton.setEnabled(false);
        postButton.setText("Posting...");

        apiService.createPost(postRequest).enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostsResponse> call, @NonNull Response<PostsResponse> response) {
                postButton.setEnabled(true);
                postButton.setText("Post");
                if (response.isSuccessful()) {
                    Toast.makeText(PostFormActivity.this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e(TAG, "Failed to create post: " + response.errorBody());
                    Toast.makeText(PostFormActivity.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostsResponse> call, @NonNull Throwable t) {
                postButton.setEnabled(true);
                postButton.setText("Post");
                Log.e(TAG, "Error creating post: " + t.getMessage());
                Toast.makeText(PostFormActivity.this, "Error creating post", Toast.LENGTH_SHORT).show();
            }
        });
    }
}