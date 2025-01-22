package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 確保你的佈局文件名是 activity_main.xml

        // 獲取佈局元素
        ImageButton startTrainingButton = findViewById(R.id.startTrainingButton);
        ImageButton communityButton = findViewById(R.id.communityButton);
        ImageButton competitionButton = findViewById(R.id.competitionButton);
        ImageButton profileIcon = findViewById(R.id.profileIcon);
        TextView welcomeText = findViewById(R.id.welcomeText);
        RelativeLayout rootLayout = findViewById(R.id.root_layout); // 確保根佈局有 ID

        // 設置歡迎信息
        welcomeText.setText("Welcome to the AI-Powered Sports Platform");

        // 使用 WindowInsets 來處理安全區域
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(v.getPaddingLeft(), topInset, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // 設置按鈕點擊事件
       /* startTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 導航到訓練活動
                Intent intent = new Intent(MainActivity.this, TrainingActivity.class);
                startActivity(intent);
            }
        }); */

        /* communityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 導航到社區活動
                Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        }); */

        /* competitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 導航到比賽管理活動
                Intent intent = new Intent(MainActivity.this, CompetitionActivity.class);
                startActivity(intent);
            }
        }); */

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 根據用戶的登錄狀態導航到相應的活動
                if (userIsLoggedIn()) { // 檢查用戶登錄狀態
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    // 假設的用戶登錄狀態檢查方法
    private boolean userIsLoggedIn() {
        // 根據你的邏輯返回登錄狀態
        return false; // 預設為未登錄
    }
}