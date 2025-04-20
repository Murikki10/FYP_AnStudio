package com.example.fyp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // 定義 XML 中的視圖
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;

    // API 服務
    private ApiService apiService;

    // 加載提示框
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化視圖
        initViews();

        // 初始化 API Service
        apiService = ApiClient.getClient().create(ApiService.class);

        // 添加即時檢查
        setupFieldListeners();

        // 註冊按鈕點擊事件
        registerButton.setOnClickListener(v -> {
            if (validateInput()) {
                registerUser();
            }
        });

        // 初始禁用註冊按鈕
        updateRegisterButtonState();
    }

    // 初始化視圖
    private void initViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.passwordConfirm);
        registerButton = findViewById(R.id.registerButton);

        // 初始化加載提示框
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
    }

    // 註冊欄位的即時檢查
    private void setupFieldListeners() {
        // 為每個欄位添加焦點變化和文本變化監聽器
        usernameEditText.addTextChangedListener(fieldWatcher);
        emailEditText.addTextChangedListener(fieldWatcher);
        passwordEditText.addTextChangedListener(fieldWatcher);
        confirmPasswordEditText.addTextChangedListener(fieldWatcher);

        usernameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // 當焦點移開時檢查內容
                validateUsername();
            }
        });

        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateEmail();
            }
        });

        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePassword();
            }
        });

        confirmPasswordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateConfirmPassword();
            }
        });
    }

    // 文本變化監聽器，用於動態更新註冊按鈕狀態
    private final TextWatcher fieldWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateRegisterButtonState(); // 每次文本變化時更新按鈕狀態
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    // 更新註冊按鈕的狀態（啟用或禁用）
    private void updateRegisterButtonState() {
        boolean isReady = !usernameEditText.getText().toString().trim().isEmpty() &&
                !emailEditText.getText().toString().trim().isEmpty() &&
                !passwordEditText.getText().toString().trim().isEmpty() &&
                !confirmPasswordEditText.getText().toString().trim().isEmpty();

        registerButton.setEnabled(isReady);
        registerButton.setAlpha(isReady ? 1f : 0.5f); // 灰色效果
    }

    // 驗證用戶名
    private boolean validateUsername() {
        String username = usernameEditText.getText().toString().trim();
        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
            return false;
        }
        return true;
    }

    // 驗證電郵
    private boolean validateEmail() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            return false;
        }
        return true;
    }

    // 驗證密碼
    private boolean validatePassword() {
        String password = passwordEditText.getText().toString().trim();
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }
        if (!password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{6,}$")) {
            passwordEditText.setError("Password must include uppercase, lowercase, number, and special character");
            return false;
        }
        return true;
    }

    // 驗證確認密碼
    private boolean validateConfirmPassword() {
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    // 驗證所有欄位
    private boolean validateInput() {
        return validateUsername() && validateEmail() && validatePassword() && validateConfirmPassword();
    }

    // 發送註冊請求
    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        progressDialog.show();

        RegisterRequest registerRequest = new RegisterRequest(username, email, password);

        apiService.registerUser(registerRequest).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    showSnackbar("Registration Successful!", Color.GREEN);
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        showSnackbar("Registration failed: " + errorBody, Color.RED);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showSnackbar("An error occurred", Color.RED);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                showSnackbar("Network error: " + t.getMessage(), Color.RED);
            }
        });
    }

    // 顯示提示消息
    private void showSnackbar(String message, int color) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(color);

        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);

        snackbar.show();
    }
}