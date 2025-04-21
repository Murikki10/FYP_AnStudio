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

    public EventListFragment() {
        // 空的構造函數
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        // 初始化視圖
        recyclerView = view.findViewById(R.id.recyclerViewEvents);
        progressBar = view.findViewById(R.id.progressBar);
        noEventsTextView = view.findViewById(R.id.noEventsTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 加載活動數據
        fetchEvents();

        return view;
    }

    private void fetchEvents() {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        apiService.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Event> events = response.body();

                    if (events.isEmpty()) {
                        noEventsTextView.setVisibility(View.VISIBLE);
                    } else {
                        noEventsTextView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        setupEventAdapter(events);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupEventAdapter(List<Event> events) {
        eventAdapter = new EventAdapter(getContext(), events, event -> {
            EventDetailsFragment detailsFragment = new EventDetailsFragment();
            Bundle args = new Bundle();
            args.putInt("event_id", event.getEventId());
            detailsFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(eventAdapter);
    }
}