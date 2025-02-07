package com.example.fyp;

import static com.example.fyp.NavBarHelper.setupNavBar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private ImageButton navTraining;
    private ImageButton navCommunity;
    private ImageButton navCompetition;
    private ImageButton navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        initializeViews();

        // 設置導航欄
        NavBarHelper.setupNavBar(this);
    }

    private void initializeViews() {
        // 找到页面中的控件
        welcomeText = findViewById(R.id.welcomeText);

        // 初始化导航栏按钮
        navTraining = findViewById(R.id.nav_training);
        navCommunity = findViewById(R.id.nav_community);
        navCompetition = findViewById(R.id.nav_competition);
        navProfile = findViewById(R.id.nav_profile);
    }


    private void updateProfileButton() {
        if (LoginManager.isLoggedIn(this)) {
            // 如果已登录，显示用户资料图标，跳转到 Profile 页面
            navProfile.setImageResource(R.drawable.ic_profile); // 替换为已登录图标
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        } else {
            // 如果未登录，显示登录图标，跳转到登录页面
            navProfile.setImageResource(R.drawable.default_avatar); // 替换为未登录图标
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次页面重新加载时更新 Profile 按钮
        updateProfileButton();
    }
}