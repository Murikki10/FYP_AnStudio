package com.example.fyp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class EventHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加載布局文件
        View view = inflater.inflate(R.layout.fragment_event_home, container, false);

        // 獲取按鈕
        Button btnEventList = view.findViewById(R.id.btnEventList);
        Button btnRegistration = view.findViewById(R.id.btnRegistration);
        Button btnLiveStream = view.findViewById(R.id.btnLiveStream);

        // 活動列表按鈕點擊事件
        btnEventList.setOnClickListener(v -> {
            // 替換 Fragment 為 EventListFragment
            replaceFragment(new EventListFragment());
        });

        // 報名記錄按鈕點擊事件
        btnRegistration.setOnClickListener(v -> {
            // 替換 Fragment 為 RegistrationRecordFragment
            replaceFragment(new RegisteredEventsFragment());
        });

        // 直播入口按鈕點擊事件
        btnLiveStream.setOnClickListener(v -> {
            // 替換 Fragment 為 LiveStreamFragment
           // replaceFragment(new LiveStreamFragment());
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        // 使用 MainActivity 的 FragmentManager 進行 Fragment 替換
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // 替換 MainActivity 的容器
        transaction.addToBackStack(null); // 添加到返回棧
        transaction.commit();
    }
}