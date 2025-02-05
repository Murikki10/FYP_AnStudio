package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, emailInput, phoneInput;
    private Button saveButton;

    private String originalFirstName, originalLastName, originalEmail, originalPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 初始化控件
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        saveButton = findViewById(R.id.saveButton);

        // 加載用戶現有資料
        loadUserData();

        // 保存按鈕點擊事件
        saveButton.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        originalFirstName = sharedPreferences.getString("first_name", "");
        originalLastName = sharedPreferences.getString("last_name", "");
        originalEmail = sharedPreferences.getString("email", "");
        originalPhone = sharedPreferences.getString("phone", "");

        // 將現有資料填入輸入框
        firstNameInput.setText(originalFirstName);
        lastNameInput.setText(originalLastName);
        emailInput.setText(originalEmail);
        phoneInput.setText(originalPhone);
    }

    private void saveProfileChanges() {
        String newFirstName = firstNameInput.getText().toString().trim();
        String newLastName = lastNameInput.getText().toString().trim();
        String newEmail = emailInput.getText().toString().trim();
        String newPhone = phoneInput.getText().toString().trim();

        // 判斷哪些字段被修改
        boolean isFirstNameChanged = !TextUtils.equals(newFirstName, originalFirstName);
        boolean isLastNameChanged = !TextUtils.equals(newLastName, originalLastName);
        boolean isEmailChanged = !TextUtils.equals(newEmail, originalEmail);
        boolean isPhoneChanged = !TextUtils.equals(newPhone, originalPhone);

        // 如果沒有任何變更，提示用戶
        if (!isFirstNameChanged && !isLastNameChanged && !isEmailChanged && !isPhoneChanged) {
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // 構建請求對象，僅包含被修改的字段
        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest(
                isFirstNameChanged ? newFirstName : null,
                isLastNameChanged ? newLastName : null,
                isEmailChanged ? newEmail : null,
                isPhoneChanged ? newPhone : null
        );

        // 調用 API 提交變更
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateProfile("Bearer " + getToken(), updateRequest)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("auth_token", "");
    }
}