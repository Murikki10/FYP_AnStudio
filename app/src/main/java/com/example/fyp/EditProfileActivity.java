package com.example.fyp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private static final int REQUEST_CODE_PICK_IMAGE = 100;

    // UI Components
    private CircleImageView avatarImageView;
    private MaterialTextView userNameTextView, userEmailTextView;
    private TextInputEditText firstNameEditText, lastNameEditText, phoneEditText;
    private MaterialButton changeAvatarButton, saveProfileButton;

    // 用戶 Token
    private String token = "Bearer YOUR_AUTH_TOKEN_HERE"; // 從登錄功能中獲取 Token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 初始化 UI
        avatarImageView = findViewById(R.id.avatarImageView);
        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        changeAvatarButton = findViewById(R.id.changeAvatarButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        // 加載用戶數據
        loadUserData();

        // 設置按鈕點擊事件
        changeAvatarButton.setOnClickListener(v -> openImagePicker());
        saveProfileButton.setOnClickListener(v -> saveProfileChanges());
    }

    /**
     * 從後端加載用戶個人資料
     */
    private void loadUserData() {
        // 动态获取用户令牌
        String token = AuthManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用后端 API 获取用户资料
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserProfileResponse> call = apiService.getUserProfile("Bearer " + token);

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // 更新用户界面
                    updateUI(response.body().getData());
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error loading user data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUI(UserProfile user) {
        // 更新 UI 元素
        userNameTextView.setText(user.getUserName());
        userEmailTextView.setText(user.getEmail());
        firstNameEditText.setText(user.getFirstName());
        lastNameEditText.setText(user.getLastName());
        phoneEditText.setText(user.getPhone());

        // 儲存原始資料，用於後續比對
        originalFirstName = user.getFirstName();
        originalLastName = user.getLastName();
        originalPhone = user.getPhone();

        // 加載用戶頭像
        Glide.with(this)
                .load(user.getAvatarUrl() != null ? user.getAvatarUrl() : R.drawable.default_avatar)
                .placeholder(R.drawable.default_avatar)
                .into(avatarImageView);
    }

    /**
     * 打開圖片選擇器
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    /**
     * 處理圖片選擇結果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                uploadProfilePicture(selectedImageUri);
            }
        }
    }

    /**
     * 壓縮圖片並上傳到服務端
     */
    private void uploadProfilePicture(Uri imageUri) {
        File compressedImage = compressImage(imageUri);
        if (compressedImage == null) {
            Toast.makeText(this, "Fail Press PIC", Toast.LENGTH_SHORT).show();
            return;
        }

        // 創建 RequestBody 和 MultipartBody.Part
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), compressedImage);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profilePicture", compressedImage.getName(), requestFile);

        // 調用 API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<JsonObject> call = apiService.uploadProfilePicture(body);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String avatarUrl = response.body().get("avatarUrl").getAsString();
                    Glide.with(EditProfileActivity.this).load(avatarUrl).into(avatarImageView);
                    Toast.makeText(EditProfileActivity.this, "Icon Update Sucess", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Icon Update Failed。", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Upload Failed：" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 壓縮圖片
     */
    private File compressImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            File compressedFile = new File(getCacheDir(), "compressed_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(compressedFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.close();
            inputStream.close();
            return compressedFile;
        } catch (Exception e) {
            Log.e(TAG, "Fail Press PIC：" + e.getMessage());
            return null;
        }
    }

    /**
     * 保存用戶資料更改
     */
    private void saveProfileChanges() {
        JsonObject userProfile = new JsonObject();

        // 比較用戶當前輸入與原始值，只有發生更改時才加入請求
        String currentFirstName = firstNameEditText.getText().toString().trim();
        if (!currentFirstName.equals(originalFirstName)) {
            userProfile.addProperty("firstName", currentFirstName);
        }

        String currentLastName = lastNameEditText.getText().toString().trim();
        if (!currentLastName.equals(originalLastName)) {
            userProfile.addProperty("lastName", currentLastName);
        }

        String currentPhone = phoneEditText.getText().toString().trim();
        if (!currentPhone.equals(originalPhone)) {
            userProfile.addProperty("phone", currentPhone);
        }

        // 如果沒有任何更改，提示用戶並退出
        if (userProfile.entrySet().isEmpty()) {
            Toast.makeText(this, "No changes to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 動態獲取令牌
        String token = AuthManager.getAuthToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Authentication token missing. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 調用後端 API 更新用戶資料
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<JsonObject> call = apiService.updateUserProfile("Bearer " + token, userProfile);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null && response.body().get("success").getAsBoolean()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    loadUserData(); // 重新加載用戶資料
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error updating profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }// 用於保存原始的用戶資料（從後端加載）
    private String originalFirstName = "";
    private String originalLastName = "";
    private String originalPhone = "";

}