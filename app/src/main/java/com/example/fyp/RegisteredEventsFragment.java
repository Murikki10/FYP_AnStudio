package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisteredEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RegisteredEventsAdapter adapter;
    private List<Event> registeredEvents = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView noEventsTextView;

    public RegisteredEventsFragment() {
        // 空的构造函数
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载布局
        View view = inflater.inflate(R.layout.fragment_registered_events, container, false);

        // 初始化视图
        progressBar = view.findViewById(R.id.progressBar);
        noEventsTextView = view.findViewById(R.id.noEventsTextView);
        recyclerView = view.findViewById(R.id.recyclerViewEvents);

        // 设置 RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RegisteredEventsAdapter(registeredEvents, this::onEventClick);
        recyclerView.setAdapter(adapter);

        // 加载注册活动
        fetchRegisteredEvents();

        return view;
    }

    /**
     * 点击活动时跳转到活动详情页面
     */
    private void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putInt("event_id", event.getEventId());
        bundle.putString("qr_code", event.getQrCode()); // 传递 QR Code 给详情页

        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * 从后端获取注册的活动列表
     */
    private void fetchRegisteredEvents() {
        // 创建 API Service 实例
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // 从本地存储中获取 Token
        String token = getAuthToken();
        if (token == null) {
            showToast("Authentication token is missing. Please log in again.");
            return;
        }

        // 显示加载进度条
        showLoading(true);

        // 发送请求获取注册活动
        apiService.getRegisteredEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                showLoading(false); // 隐藏加载进度条

                if (response.isSuccessful() && response.body() != null) {
                    // 更新活动列表
                    registeredEvents.clear();
                    registeredEvents.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    // 显示或隐藏“无活动”消息
                    toggleNoEventsMessage(registeredEvents.isEmpty());
                } else {
                    handleErrorResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                showLoading(false); // 隐藏加载进度条
                showToast("Error: Unable to connect to server. Please check your network.");
            }
        });
    }

    /**
     * 获取本地存储中的认证 Token
     */
    private String getAuthToken() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("auth_token", null);
    }

    /**
     * 显示或隐藏加载进度条
     */
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        noEventsTextView.setVisibility(View.GONE); // 加载时隐藏无活动消息
    }

    /**
     * 显示或隐藏“无活动”消息
     */
    private void toggleNoEventsMessage(boolean show) {
        noEventsTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * 处理 HTTP 错误响应
     */
    private void handleErrorResponse(int responseCode) {
        switch (responseCode) {
            case 401:
            case 403:
                showToast("Unauthorized access. Please log in again.");
                break;
            case 404:
                toggleNoEventsMessage(true); // 没有找到活动时显示无活动消息
                break;
            default:
                showToast("Failed to load registered events. Please try again later.");
                break;
        }
    }

    /**
     * 显示 Toast 消息
     */
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}