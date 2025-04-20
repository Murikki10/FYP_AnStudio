package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage; // 用戶頭像
    private TextView userNameText, userBioText, followersCountText, followingCountText, postsCountText;
    private ImageButton backButton, settingsButton;
    private ProgressBar loadingIndicator; // 加載指示器
    private RecyclerView eventListRecyclerView; // 活動列表
    private TextView noEventsTextView; // 無活動提示

    private EventAdapter eventAdapter; // 活動數據適配器
    private List<Event> eventList = new ArrayList<>(); // 活動數據列表
    private TextView postsCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 初始化 UI 元件
        initUI();

        // 驗證是否已登入
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        // 加載用戶資料和活動列表
        loadUserData();
        setupEventList();
        loadPostCount();

        // 註冊按鈕點擊事件
        setupClickListeners();

        clickPostCount();

    }

    // 初始化 UI 元件
    private void initUI() {
        profileImage = findViewById(R.id.profileImage);
        userNameText = findViewById(R.id.userNameText);
        userBioText = findViewById(R.id.userBioText);
        followersCountText = findViewById(R.id.followersCountText);
        followingCountText = findViewById(R.id.followingCountText);
        postsCountText = findViewById(R.id.postsCountText);
        backButton = findViewById(R.id.backButton);
        settingsButton = findViewById(R.id.settingsButton);
        loadingIndicator = findViewById(R.id.loadingIndicator); // 加載指示器
        eventListRecyclerView = findViewById(R.id.trainingRecordsRecyclerView); // 活動列表 RecyclerView
        noEventsTextView = findViewById(R.id.noEventsTextView); // 無活動提示
    }

    // 驗證是否已登入
    private boolean isUserLoggedIn() {
        String token = AuthManager.getAuthToken();
        if (!AuthManager.isLoggedIn() || token == null || token.isEmpty()) {
            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 重定向到登入頁面
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // 加載用戶資料
    private void loadUserData() {
        showLoading(true);

        String token = AuthManager.getAuthToken();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserProfileResponse> call = apiService.getUserProfile("Bearer " + token);

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    updateUI(response.body().getData());
                    loadEventList(); // 加載註冊活動列表
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ProfileActivity.this, "Error loading user data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProfileActivity", "Error: ", t);
            }
        });
    }

    // 更新用戶資料 UI
    private void updateUI(UserProfile user) {
        userNameText.setText(user.getUserName());
        userBioText.setText(user.getBio());
        followersCountText.setText("Followers: " + user.getFollowersCount());
        followingCountText.setText("Following: " + user.getFollowingCount());
        postsCountText.setText("Posts: " + user.getPostsCount());

        // 使用 Glide 加載頭像
        Glide.with(this)
                .load(user.getAvatarUrl() != null ? user.getAvatarUrl() : R.drawable.default_avatar)
                .placeholder(R.drawable.default_avatar)
                .into(profileImage);
    }

    // 初始化活動列表 RecyclerView
    private void setupEventList() {
        eventListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(this, eventList, event -> {
            // 點擊活動項目，跳轉到活動詳情頁
            Intent intent = new Intent(ProfileActivity.this, EventDetailsFragment.class);
            intent.putExtra("event_id", event.getEventId());
            startActivity(intent);
        });
        eventListRecyclerView.setAdapter(eventAdapter);
    }

    // 加載註冊活動列表
    private void loadEventList() {
        showLoading(true);

        String token = AuthManager.getAuthToken();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getRegisteredEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    eventList.addAll(response.body());
                    eventAdapter.notifyDataSetChanged();

                    if (eventList.isEmpty()) {
                        noEventsTextView.setVisibility(View.VISIBLE); // 顯示「無活動」提示
                        eventListRecyclerView.setVisibility(View.GONE); // 隱藏 RecyclerView
                    } else {
                        noEventsTextView.setVisibility(View.GONE); // 隱藏「無活動」提示
                        eventListRecyclerView.setVisibility(View.VISIBLE); // 顯示 RecyclerView
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load events.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(ProfileActivity.this, "Error loading events: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostCount() {
        String token = AuthManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 創建 API 客戶端
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<PostCountResponse> call = apiService.getPostCount("Bearer " + token);

        // 發送 API 請求
        call.enqueue(new Callback<PostCountResponse>() {
            @Override
            public void onResponse(Call<PostCountResponse> call, Response<PostCountResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // 從響應中獲取帖子數量
                    int postCount = response.body().getData().getPostsCount();
                    updatePostCount(postCount); // 更新帖子數量到 UI
                } else {
                    // 處理失敗的情況
                    Toast.makeText(ProfileActivity.this, "Failed to load post count.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostCountResponse> call, Throwable t) {
                // 處理請求失敗的情況
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePostCount(int postCount) {
        postsCountText.setText("Posts: " + postCount);
    }

    // 顯示或隱藏加載指示器
    private void showLoading(boolean isLoading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
    private void showPostsDialog() {
        // 创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_posts_list, null);
        builder.setView(dialogView);

        // 初始化对话框中的 RecyclerView
        RecyclerView postsRecyclerView = dialogView.findViewById(R.id.postsRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 创建适配器
        GenericAdapter<Post> adapter = new GenericAdapter<>(new ArrayList<>(), (textView, post) -> {
            textView.setText(post.getTitle()); // 将帖子标题绑定到 TextView
            textView.setTextSize(16f);
        });
        postsRecyclerView.setAdapter(adapter);

        // 显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

        // 加载真实数据
        loadUserPosts(adapter);
    }
    private void loadUserPosts(GenericAdapter<Post> adapter) {
        String token = AuthManager.getAuthToken();
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
                    adapter.updateItems(posts); // 更新适配器数据
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load posts.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserPostsResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // 註冊按鈕點擊事件
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());
        settingsButton.setOnClickListener(v -> openSettings());
    }
    private void clickPostCount() {
        postsCountTextView = findViewById(R.id.postsCountText);
        postsCountTextView.setOnClickListener(v -> {
            // 點擊時彈出對話框
            showPostsDialog();
        });
    }
    // 打開設定頁面
    private void openSettings() {
        Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}