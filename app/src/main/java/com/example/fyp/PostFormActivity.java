package com.example.fyp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostFormActivity extends AppCompatActivity {

    private static final String TAG = "PostFormActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_REQUEST_CODE = 1000;

    private Spinner boardSpinner;
    private Button postButton, selectImageButton;
    private RichEditor richEditor;
    private ChipGroup tagChipGroup;
    private ImageView selectedImagePreview;
    private Uri selectedImageUri;
    private String selectedImageUrl;
    private ProgressDialog progressDialog;

    private List<Tag> tagOptions = new ArrayList<>(); // 從後端獲取的標籤
    private List<Integer> selectedTagIds = new ArrayList<>(); // 選中的標籤 ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_form);

        // 初始化 UI 元素
        initializeUI();

        // 檢查存儲權限
        checkStoragePermission();

        // 加載分區和標籤數據
        fetchBoards();
        fetchTags();

        // 圖片選擇按鈕事件
        selectImageButton.setOnClickListener(v -> openImagePicker());

        // 發布按鈕事件
        postButton.setOnClickListener(v -> createPost());
    }

    private void initializeUI() {
        boardSpinner = findViewById(R.id.board_spinner);
        postButton = findViewById(R.id.post_button);
        selectImageButton = findViewById(R.id.upload_image_button);
        selectedImagePreview = findViewById(R.id.image_preview);
        richEditor = findViewById(R.id.rich_editor);
        tagChipGroup = findViewById(R.id.tag_chip_group);

        richEditor.setEditorHeight(200);
        richEditor.setEditorFontSize(16);
        richEditor.setPadding(10, 10, 10, 10);
        richEditor.setPlaceholder("Write something...");
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Storage permission already granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Storage permission granted");
            } else {
                Toast.makeText(this, "Storage permission is required to select images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Log.d(TAG, "Selected image URI: " + selectedImageUri);
            selectedImagePreview.setImageURI(selectedImageUri);
            selectedImagePreview.setVisibility(ImageView.VISIBLE);
            uploadImageWithCompression(selectedImageUri);
        }
    }

    private void uploadImageWithCompression(Uri imageUri) {
        File compressedImageFile = compressImage(imageUri);
        if (compressedImageFile == null) {
            Toast.makeText(this, "Failed to compress image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), compressedImageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", compressedImageFile.getName(), requestBody);

        showProgressDialog("Uploading image...");
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<JsonObject> call = apiService.uploadImage(imagePart);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                hideProgressDialog();

                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseBody = response.body();
                    if (responseBody.has("files")) {
                        JsonObject fileObject = responseBody.getAsJsonArray("files").get(0).getAsJsonObject();
                        if (fileObject.has("path") && !fileObject.get("path").isJsonNull()) {
                            String relativePath = fileObject.get("path").getAsString();
                            selectedImageUrl = "http://13.239.37.192:5001" + relativePath;
                            Log.d(TAG, "Uploaded image URL: " + selectedImageUrl);
                            Glide.with(PostFormActivity.this).load(selectedImageUrl).into(selectedImagePreview);
                        } else {
                            Log.e(TAG, "File object does not contain path or it is null");
                            Toast.makeText(PostFormActivity.this, "Failed to get uploaded image URL", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Response does not contain files array");
                        Toast.makeText(PostFormActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Image upload failed. Response code: " + response.code());
                    Toast.makeText(PostFormActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hideProgressDialog();
                Log.e(TAG, "Error uploading image: " + t.getMessage());
                Toast.makeText(PostFormActivity.this, "Error uploading image: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

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
            Log.e(TAG, "Image compression failed: " + e.getMessage());
            return null;
        }
    }

    private void fetchBoards() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getBoards().enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(Call<List<Board>> call, Response<List<Board>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayAdapter<Board> adapter = new ArrayAdapter<>(
                            PostFormActivity.this,
                            android.R.layout.simple_spinner_item,
                            response.body()
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    boardSpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Board>> call, Throwable t) {
                Log.e(TAG, "Failed to fetch boards: " + t.getMessage());
                Toast.makeText(PostFormActivity.this, "Failed to fetch boards", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTags() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getTags().enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tagOptions = response.body();
                    setupTagChips();
                } else {
                    Log.e(TAG, "Failed to load tags: " + response.errorBody());
                    Toast.makeText(PostFormActivity.this, "Failed to load tags", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                Log.e(TAG, "Error loading tags: " + t.getMessage());
                Toast.makeText(PostFormActivity.this, "Error loading tags", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTagChips() {
        tagChipGroup.removeAllViews();

        for (Tag tag : tagOptions) {
            Chip chip = new Chip(this);
            chip.setText(tag.getTagName());
            chip.setCheckable(true);

            // 選中或取消標籤的邏輯
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedTagIds.add(tag.getTagId());
                } else {
                    selectedTagIds.remove(Integer.valueOf(tag.getTagId()));
                }
            });

            tagChipGroup.addView(chip);
        }
    }

    private void createPost() {
        String title = ((EditText) findViewById(R.id.title_input)).getText().toString().trim();
        String content = richEditor.getHtml();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Board selectedBoard = (Board) boardSpinner.getSelectedItem();
        if (selectedBoard == null) {
            Toast.makeText(this, "Please select a board.", Toast.LENGTH_SHORT).show();
            return;
        }

        PostRequest postRequest = new PostRequest(
                selectedBoard.getBoardId(),
                title,
                content,
                selectedTagIds, // 選中的標籤
                selectedImageUrl,
                123 // 假設的 userId
        );

        showProgressDialog("Creating post...");
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.createPost(postRequest).enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                hideProgressDialog();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PostFormActivity.this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PostFormActivity.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                hideProgressDialog();
                Toast.makeText(PostFormActivity.this, "Error creating post: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}