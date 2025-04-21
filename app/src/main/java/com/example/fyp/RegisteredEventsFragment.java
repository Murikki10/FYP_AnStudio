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
        // 空的構造函數
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registered_events, container, false);

        // 初始化視圖
        progressBar = view.findViewById(R.id.progressBar);
        noEventsTextView = view.findViewById(R.id.noEventsTextView);
        recyclerView = view.findViewById(R.id.recyclerViewEvents);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RegisteredEventsAdapter(registeredEvents, this::onEventClick);
        recyclerView.setAdapter(adapter);

        fetchRegisteredEvents();

        return view;
    }

    private void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putInt("event_id", event.getEventId());
        bundle.putString("qr_code", event.getQrCode());

        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchRegisteredEvents() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        String token = getAuthToken();
        if (token == null) {
            showToast("Authentication token is missing. Please log in again.");
            return;
        }

        showLoading(true);

        apiService.getRegisteredEvents("Bearer " + token).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    registeredEvents.clear();
                    registeredEvents.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    toggleNoEventsMessage(registeredEvents.isEmpty());
                } else {
                    handleErrorResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                showLoading(false);
                showToast("Error: Unable to connect to server. Please check your network.");
            }
        });
    }

    private String getAuthToken() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("auth_token", null);
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        noEventsTextView.setVisibility(View.GONE);
    }

    private void toggleNoEventsMessage(boolean show) {
        noEventsTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void handleErrorResponse(int responseCode) {
        switch (responseCode) {
            case 401:
            case 403:
                showToast("Unauthorized access. Please log in again.");
                break;
            case 404:
                toggleNoEventsMessage(true);
                break;
            default:
                showToast("Failed to load registered events. Please try again later.");
                break;
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}