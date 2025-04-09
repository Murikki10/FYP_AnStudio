package com.example.fyp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment {

    private TextView textViewTitle, textViewDescription, textViewStartTime, textViewEndTime, textViewLocation;
    private Button buttonRegister;
    private int eventId;

    public EventDetailsFragment() {
        // 空的構造函數
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加載 Fragment 的佈局
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        // 初始化 Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        // 設置返回按鈕
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Event Details");
        }

        // 返回按鈕點擊事件
        toolbar.setNavigationOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack(); // 返回上一個 Fragment
        });

        // 初始化視圖
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewStartTime = view.findViewById(R.id.textViewStartTime);
        textViewEndTime = view.findViewById(R.id.textViewEndTime);
        textViewLocation = view.findViewById(R.id.textViewLocation);
        buttonRegister = view.findViewById(R.id.buttonRegister);

        // 獲取傳遞的 eventId
        if (getArguments() != null) {
            eventId = getArguments().getInt("event_id", -1);
        }

        if (eventId != -1) {
            // 加載活動詳情
            fetchEventDetails(eventId);
        }

        // 報名按鈕點擊事件
        buttonRegister.setOnClickListener(v -> registerForEvent(eventId));

        return view;
    }

    private void fetchEventDetails(int eventId) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        apiService.getEventDetails(eventId).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Event event = response.body();

                    textViewTitle.setText("Event Title: " + event.getTitle());
                    textViewDescription.setText("Event Description: " + event.getDescription());
                    textViewStartTime.setText("Start Time: " + event.getFormattedStartTime());
                    textViewEndTime.setText("End Time: " + event.getFormattedEndTime());
                    textViewLocation.setText("Location: " + event.getLocation());
                } else {
                    Toast.makeText(getContext(), "Failed to load event details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerForEvent(int eventId) {
        // 報名的實現邏輯
        Toast.makeText(getContext(), "Registering for event " + eventId, Toast.LENGTH_SHORT).show();
    }
}