package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial trainingReminderSwitch;
    private SwitchMaterial communityNotificationsSwitch;
    private Button editProfileButton;
    private Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化 SwitchMaterial 控件
        trainingReminderSwitch = findViewById(R.id.trainingReminderSwitch);
        communityNotificationsSwitch = findViewById(R.id.communityNotificationsSwitch);

        // 初始化按鈕
        editProfileButton = findViewById(R.id.editProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        // 設置開關的初始狀態
        trainingReminderSwitch.setChecked(true); // 示例
        communityNotificationsSwitch.setChecked(false); // 示例

        // 添加開關的切換事件
        trainingReminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(SettingsActivity.this,
                        "Training Reminder: " + (isChecked ? "Enabled" : "Disabled"),
                        Toast.LENGTH_SHORT).show()
        );

        communityNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(SettingsActivity.this,
                        "Community Notifications: " + (isChecked ? "Enabled" : "Disabled"),
                        Toast.LENGTH_SHORT).show()
        );

        // 設置按鈕點擊事件，跳轉到 EditProfileActivity
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // 設置按鈕點擊事件，跳轉到 ChangePasswordActivity
        changePasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }
}