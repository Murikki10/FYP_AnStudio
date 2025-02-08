package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 使用主頁 XML 布局

        // 初始化 Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化 BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 設置導航欄點擊事件
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_workout) {
                // Workout 直接跳轉到 WorkoutActivity
                Intent intent = new Intent(MainActivity.this, WorkoutActivity.class);
                startActivity(intent);
                return false; // 不加載 Fragment
            } else if (item.getItemId() == R.id.nav_community) {
                selectedFragment = new PostsListFragment();
                toolbar.setTitle("Community");
            } else if (item.getItemId() == R.id.nav_competition) {
                selectedFragment = new CompetitionFragment();
                toolbar.setTitle("Competition");
            } else if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new MainFragment();
                toolbar.setTitle("Home");
            } else if (item.getItemId() == R.id.nav_profile) {
                if (LoginManager.isLoggedIn(MainActivity.this)) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return false; // 不切換 Fragment
            }

            // 如果選中的 Fragment 不為空，執行 Fragment 替換
            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commit();
            }

            return true;
        });

        // 預設導航項目設置為 Home
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        toolbar.setTitle("Home");
    }
}