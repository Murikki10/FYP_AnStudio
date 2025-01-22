package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private TextView registerLink;
    private ProgressBar progressBar;

    // Bottom Navigation
    private ImageButton startTrainingButton;
    private ImageButton communityButton;
    private ImageButton competitionButton;
    private ImageButton profileIcon;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
        initializeApiService();
    }

    private void initializeViews() {
        // Main login views
        usernameLayout = findViewById(R.id.usernameLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        progressBar = findViewById(R.id.progressBar);

        // Bottom navigation
        startTrainingButton = findViewById(R.id.startTrainingButton);
        communityButton = findViewById(R.id.communityButton);
        competitionButton = findViewById(R.id.competitionButton);
        profileIcon = findViewById(R.id.profileIcon);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> performLogin());

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Bottom navigation listeners
        startTrainingButton.setOnClickListener(v -> navigateToTraining());
        communityButton.setOnClickListener(v -> navigateToCommunity());
        competitionButton.setOnClickListener(v -> navigateToCompetition());
        profileIcon.setOnClickListener(v -> navigateToProfile());
    }

    private void initializeApiService() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void performLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Add logging
        Log.d("LoginActivity", "Attempting login - Username: " + username);

        LoginRequest loginRequest = new LoginRequest(username, password);
        Call<LoginResponse> call = apiService.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    // Handle successful login
                    Toast.makeText(LoginActivity.this,
                            "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        // Parse error response
                        Gson gson = new Gson();
                        LoginResponse errorResponse = gson.fromJson(
                                response.errorBody().string(),
                                LoginResponse.class
                        );
                        Toast.makeText(LoginActivity.this,
                                errorResponse.getMessage(),
                                Toast.LENGTH_LONG).show();

                        // Add logging
                        Log.d("LoginActivity", "Login failed: " + errorResponse.getMessage());
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this,
                                "Login failed",
                                Toast.LENGTH_LONG).show();
                        Log.e("LoginActivity", "Error parsing error response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", "Network error", t);
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!isLoading);
        usernameEditText.setEnabled(!isLoading);
        passwordEditText.setEnabled(!isLoading);
    }

    // Navigation methods
    private void navigateToTraining() {
        Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
    }

    private void navigateToCommunity() {
        Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
    }

    private void navigateToCompetition() {
        Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
    }

    private void navigateToProfile() {
        Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
    }
}


