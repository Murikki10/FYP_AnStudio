package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView userNameTextView, userBioTextView, followersCountTextView, followingCountTextView, postsCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // 初始化 UI 元件
        initUI();

        // 從 Intent 獲取 userId
        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", -1);
        Log.d("UserProfileActivity", "Received User ID: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加載用戶資料
        loadUserProfile(userId);
    }

    private void initUI() {
        profileImageView = findViewById(R.id.profileImage);
        userNameTextView = findViewById(R.id.userNameText);
        userBioTextView = findViewById(R.id.userBioText);
        followersCountTextView = findViewById(R.id.followersCountText);
        followingCountTextView = findViewById(R.id.followingCountText);
        postsCountTextView = findViewById(R.id.postsCountText);
    }

    private void loadUserProfile(int userId) {
        String token = AuthManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserProfileResponse> call = apiService.getUserProfile("Bearer " + token, userId);

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile user = response.body().getUser(); // 正確獲取用戶數據
                    if (user != null) {
                        updateUI(user); // 更新 UI
                    } else {
                        Toast.makeText(UserProfileActivity.this, "User data is null.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "Failed to load user profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "Error loading user profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UserProfileActivity", "Error: ", t);
            }
        });
    }

    private void updateUI(UserProfile user) {
        userNameTextView.setText(user.getUserName());
        userBioTextView.setText(user.getBio());
        followersCountTextView.setText("Followers: " + user.getFollowersCount());
        followingCountTextView.setText("Following: " + user.getFollowingCount());
        postsCountTextView.setText("Posts: " + user.getPostsCount());

        // 加載用戶頭像
        Glide.with(this)
                .load(user.getAvatarUrl() != null ? user.getAvatarUrl() : R.drawable.default_avatar)
                .placeholder(R.drawable.default_avatar)
                .into(profileImageView);
    }
}