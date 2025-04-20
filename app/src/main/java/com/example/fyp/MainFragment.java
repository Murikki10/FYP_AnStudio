package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

public class MainFragment extends Fragment {

    private FrameLayout advertisementView;
    private ImageView advertisementImageView;
    private EditText searchEditText;
    private RecyclerView searchResultRecyclerView;
    private SearchResultAdapter searchResultAdapter;

    private List<Integer> adImages; // 廣告圖片資源 ID
    private int currentAdIndex = 0;
    private Handler adHandler = new Handler();
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 初始化 UI 元件
        advertisementView = view.findViewById(R.id.advertisementView);
        searchEditText = view.findViewById(R.id.searchEditText);
        searchResultRecyclerView = view.findViewById(R.id.searchResultRecyclerView);

        // 初始化 RecyclerView
        setupRecyclerView();

        // 初始化廣告輪播
        initializeAdImages();
        setupAdvertisementView();

        // 設置搜索框的事件
        setupSearchBar();

        return view;
    }

    /**
     * 初始化廣告圖片資源
     */
    private void initializeAdImages() {
        adImages = new ArrayList<>();
        adImages.add(R.drawable.ad_image1); // 替換為您的圖片資源
        adImages.add(R.drawable.ad_image2);
        adImages.add(R.drawable.ad_image3);
    }

    /**
     * 設置廣告輪播邏輯
     */
    private void setupAdvertisementView() {
        advertisementImageView = new ImageView(getContext());
        advertisementImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // 動態設置高度為屏幕高度的 1/3
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int adHeight = screenHeight / 3; // 設置為屏幕高度的 1/3
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, adHeight);
        advertisementImageView.setLayoutParams(layoutParams);

        advertisementView.addView(advertisementImageView);

        // 開啟輪播
        startAdCarousel();
    }

    /**
     * 開啟廣告輪播
     */
    private void startAdCarousel() {
        adHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 切換圖片
                advertisementImageView.setImageResource(adImages.get(currentAdIndex));
                currentAdIndex = (currentAdIndex + 1) % adImages.size();

                // 每 3 秒切換一次
                adHandler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    /**
     * 初始化 RecyclerView
     */
    private void setupRecyclerView() {
        searchResultAdapter = new SearchResultAdapter(new ArrayList<>(), this::onSearchResultClick);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultRecyclerView.setAdapter(searchResultAdapter);
    }

    /**
     * 設置搜索欄邏輯
     */
    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 不需要
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 每次輸入時重置搜索計時
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // 延遲執行搜索請求
                searchRunnable = () -> performSearch(s.toString());
                searchHandler.postDelayed(searchRunnable, 500); // 延遲 500ms
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // 不需要
            }
        });
    }

    /**
     * 執行搜索請求
     */
    private void performSearch(String query) {
        if (query.isEmpty()) {
            searchResultRecyclerView.setVisibility(View.GONE);
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<UserProfile>> call = apiService.searchUsers(query);

        call.enqueue(new Callback<List<UserProfile>>() {
            @Override
            public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserProfile> results = response.body();
                    searchResultAdapter.updateResults(results);

                    // 根據結果顯示或隱藏列表
                    if (results.isEmpty()) {
                        searchResultRecyclerView.setVisibility(View.GONE);
                    } else {
                        searchResultRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "No results found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                Toast.makeText(getContext(), "Search failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 點擊搜索結果
     */
    private void onSearchResultClick(UserProfile user) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra("userId", user.getUserId()); // 傳遞 userId
        startActivity(intent);
    }
}