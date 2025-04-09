package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

        // 初始化按鈕
        buttonRegister = view.findViewById(R.id.buttonRegister);

        // 禁用按鈕，直到加載完成
        if (buttonRegister != null) {
            buttonRegister.setEnabled(false);
        } else {
            throw new IllegalStateException("buttonRegister is not initialized. Check the layout file.");
        }

        // 初始化其他視圖
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewStartTime = view.findViewById(R.id.textViewStartTime);
        textViewEndTime = view.findViewById(R.id.textViewEndTime);
        textViewLocation = view.findViewById(R.id.textViewLocation);

        // 獲取傳遞的 eventId
        if (getArguments() != null) {
            eventId = getArguments().getInt("event_id", -1);
        }

        if (eventId != -1) {
            // 加載活動詳情
            fetchEventDetails(eventId);
        }

        // 報名按鈕點擊事件
        buttonRegister.setOnClickListener(v -> {
            String userToken = "YOUR_USER_TOKEN"; // 替換成實際的用戶 Token
            registerForEvent(eventId, userToken);
        });

        return view;
    }

    private void fetchEventDetails(int eventId) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        apiService.getEventDetails(eventId).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Event event = response.body();

                    // 更新 UI
                    textViewTitle.setText("Event Title: " + event.getTitle());
                    textViewDescription.setText("Event Description: " + event.getDescription());
                    textViewStartTime.setText("Start Time: " + event.getFormattedStartTime());
                    textViewEndTime.setText("End Time: " + event.getFormattedEndTime());
                    textViewLocation.setText("Location: " + event.getLocation());

                    // 檢查用戶是否已註冊
                    boolean isRegistered = event.isRegistered(); // 假設 Event 類有 isRegistered 方法
                    if (isRegistered) {
                        // 用戶已註冊，禁用按鈕並更新文本
                        buttonRegister.setText("Registered");
                        buttonRegister.setEnabled(false);
                    } else {
                        // 用戶未註冊，顯示註冊按鈕
                        buttonRegister.setText("Register");
                        buttonRegister.setEnabled(true);
                    }
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

    private void registerForEvent(int eventId, String userToken) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // 構建請求體
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("userToken", userToken); // 傳遞用戶 Token
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                requestBodyJson.toString()
        );

        apiService.registerEvent(eventId, requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // 從響應中解析 QR Code
                        JSONObject responseBody = new JSONObject(response.body().string());
                        if (responseBody.has("qr_code")) {
                            String qrCode = responseBody.getString("qr_code");
                            Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                            showQRCode(qrCode); // 顯示 QR Code
                        } else {
                            Toast.makeText(getContext(), "Registration successful, but no QR Code returned.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(getContext(), "Authentication token required!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 403) {
                    Toast.makeText(getContext(), "Invalid or expired token!", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 409) {
                    Toast.makeText(getContext(), "You have already registered for this event.", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to register for the event.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // 自定義方法顯示 QR Code
    private void showQRCode(String qrCode) {
        // 獲取 QR Code ImageView
        ImageView qrCodeImageView = getView().findViewById(R.id.qrCodeImageView);

        // 將 Base64 編碼的 QR Code 轉換為 Bitmap
        byte[] decodedString = Base64.decode(qrCode.split(",")[1], Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        // 在 ImageView 中顯示 QR Code
        qrCodeImageView.setImageBitmap(decodedByte);

        // 確保 ImageView 可見
        qrCodeImageView.setVisibility(View.VISIBLE);
    }
}