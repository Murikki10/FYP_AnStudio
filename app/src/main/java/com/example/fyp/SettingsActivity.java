package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial trainingReminderSwitch; // 訓練提醒開關
    private SwitchMaterial communityNotificationsSwitch; // 社群通知開關
    private Button editProfileButton; // 編輯個人資料按鈕
    private Button changePasswordButton; // 更改密碼按鈕
    private Button logoutButton; // 登出按鈕

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化控件
        trainingReminderSwitch = findViewById(R.id.trainingReminderSwitch);
        communityNotificationsSwitch = findViewById(R.id.communityNotificationsSwitch);
        editProfileButton = findViewById(R.id.editProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        logoutButton = findViewById(R.id.logoutButton);

        // 檢查是否已登入
        if (!AuthManager.isLoggedIn()) {
            // 如果未登入，跳轉到登入頁
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        // 設置開關的初始狀態 (這裡使用假設的默認值，未來可結合實際功能從後端或本地存儲讀取)
        trainingReminderSwitch.setChecked(true); // 默認啟用訓練提醒
        communityNotificationsSwitch.setChecked(false); // 默認禁用社群通知

        // 添加開關的切換事件處理器
        trainingReminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 顯示當前開關狀態的提示 (未來可以在這裡實現實際功能)
            Toast.makeText(SettingsActivity.this,
                    "Training Reminder: " + (isChecked ? "Enabled" : "Disabled"),
                    Toast.LENGTH_SHORT).show();

            // TODO: 在這裡處理訓練提醒開關的功能
        });

        communityNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 顯示當前開關狀態的提示 (未來可以在這裡實現實際功能)
            Toast.makeText(SettingsActivity.this,
                    "Community Notifications: " + (isChecked ? "Enabled" : "Disabled"),
                    Toast.LENGTH_SHORT).show();

            // TODO: 在這裡處理社群通知開關的功能
        });

        // 設置 "編輯個人資料" 按鈕的點擊事件
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // 設置 "更改密碼" 按鈕的點擊事件
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // 設置登出按鈕的點擊事件
        logoutButton.setOnClickListener(v -> logout());
    }

    // 登出方法
    private void logout() {
        // 清除登入狀態
        AuthManager.logout();

        // 跳轉到登入頁面
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        // 顯示提示消息
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
}