package com.example.fyp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private ProgressBar progressBar;
    private TextView noEventsTextView;

    // 空的構造函數（必要）
    public EventListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 為 Fragment 加載佈局
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        // 初始化 Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        // 啟用返回按鈕
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Event List");
        }

        // 返回按鈕點擊事件
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 初始化視圖
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        progressBar = view.findViewById(R.id.progressBar);
        noEventsTextView = view.findViewById(R.id.noEventsTextView);

        // 設置 RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 加載活動數據
        fetchEvents();

        return view;
    }

    private void fetchEvents() {
        // 顯示加載進度條
        progressBar.setVisibility(View.VISIBLE);

        // 初始化 API Service
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // 調用 API 獲取活動列表
        apiService.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                // 隱藏進度條
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Event> events = response.body();

                    if (events.isEmpty()) {
                        // 如果沒有活動，顯示消息
                        noEventsTextView.setVisibility(View.VISIBLE);
                    } else {
                        // 隱藏 "無活動" 消息並顯示活動列表
                        noEventsTextView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        // 設置適配器
                        setupEventAdapter(events);
                    }
                } else {
                    // 處理失敗響應
                    Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                // 隱藏進度條並顯示錯誤消息
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void setupEventAdapter(List<Event> events) {
        // 初始化適配器
        eventAdapter = new EventAdapter(getContext(), events, event -> {
            // 點擊事件：跳轉到 EventDetailsFragment
            EventDetailsFragment detailsFragment = new EventDetailsFragment();
            Bundle args = new Bundle();
            args.putInt("event_id", event.getEventId()); // 傳遞活動 ID
            detailsFragment.setArguments(args);

            // 顯示 EventDetailsFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment) // 替換當前 Fragment
                    .addToBackStack(null) // 添加到返回棧
                    .commit();
        });

        // 設置適配器
        recyclerView.setAdapter(eventAdapter);
    }
}