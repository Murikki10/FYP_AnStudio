package com.example.fyp;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class EventHomeFragment extends Fragment {

    private LinearLayout rootLayout;
    private Button btnEventList;
    private Button btnRegisteredEvents;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 創建根佈局（LinearLayout）
        rootLayout = new LinearLayout(getContext());
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        // 創建切換按鈕
        setupSwitchButtons();

        // 創建 Fragment 容器
        setupFragmentContainer();

        // 默認顯示 EventListFragment
        switchToFragment(new EventListFragment(), btnEventList);

        return rootLayout;
    }

    /**
     * 創建切換按鈕
     */
    private void setupSwitchButtons() {
        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // 創建 "Event List" 按鈕
        btnEventList = new Button(getContext());
        btnEventList.setText("Event List");
        btnEventList.setLayoutParams(new LinearLayout.LayoutParams(
                0, // 權重分配
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1 // 權重
        ));
        btnEventList.setOnClickListener(v -> switchToFragment(new EventListFragment(), btnEventList));

        // 創建 "Registered Events" 按鈕
        btnRegisteredEvents = new Button(getContext());
        btnRegisteredEvents.setText("Registered Events");
        btnRegisteredEvents.setLayoutParams(new LinearLayout.LayoutParams(
                0, // 權重分配
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1 // 權重
        ));
        btnRegisteredEvents.setOnClickListener(v -> switchToFragment(new RegisteredEventsFragment(), btnRegisteredEvents));

        // 添加按鈕到佈局
        buttonLayout.addView(btnEventList);
        buttonLayout.addView(btnRegisteredEvents);

        // 添加按鈕佈局到根佈局
        rootLayout.addView(buttonLayout);
    }

    /**
     * 創建 Fragment 容器
     */
    private void setupFragmentContainer() {
        FrameLayout fragmentContainer = new FrameLayout(getContext());
        fragmentContainer.setId(View.generateViewId()); // 動態生成 ID
        fragmentContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        // 添加 Fragment 容器到根佈局
        rootLayout.addView(fragmentContainer);
    }

    /**
     * 切換 Fragment 並更新按鈕狀態
     */
    private void switchToFragment(Fragment fragment, Button activeButton) {
        // 替換 Fragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(rootLayout.getChildAt(1).getId(), fragment) // 使用 Fragment 容器 ID
                .commit();

        // 更新按鈕樣式
        updateButtonStyles(activeButton);
    }

    /**
     * 更新按鈕樣式
     */
    private void updateButtonStyles(Button activeButton) {
        // 重置按鈕樣式
        btnEventList.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnEventList.setTextColor(getResources().getColor(android.R.color.white));

        btnRegisteredEvents.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnRegisteredEvents.setTextColor(getResources().getColor(android.R.color.white));

        // 設置當前按鈕的高亮樣式
        activeButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        activeButton.setTextColor(getResources().getColor(android.R.color.white));
    }
}