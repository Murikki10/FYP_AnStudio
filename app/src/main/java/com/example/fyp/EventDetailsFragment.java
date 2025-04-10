package com.example.fyp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsFragment extends Fragment {

    private static final String TAG = "EventDetailsFragment";

    private TextView textViewTitle, textViewDescription, textViewStartTime, textViewEndTime, textViewLocation;
    private Button buttonRegister;
    private ImageView qrCodeImageView;
    private int eventId;

    public EventDetailsFragment() {
        // 空构造函数
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载 Fragment 的布局
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        // 初始化视图
        buttonRegister = view.findViewById(R.id.buttonRegister);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewStartTime = view.findViewById(R.id.textViewStartTime);
        textViewEndTime = view.findViewById(R.id.textViewEndTime);
        textViewLocation = view.findViewById(R.id.textViewLocation);
        qrCodeImageView = view.findViewById(R.id.qrCodeImageView);

        // 默认隐藏 QR Code
        qrCodeImageView.setVisibility(View.GONE);

        // 获取传递的 eventId
        if (getArguments() != null) {
            eventId = getArguments().getInt("event_id", -1);
        }

        if (eventId != -1) {
            // 加载活动详情
            fetchEventDetails(eventId);
        }

        // 注册按钮点击事件
        buttonRegister.setOnClickListener(v -> {
            String userToken = "YOUR_USER_TOKEN"; // 替换成实际用户的 Token
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

                    // 打印调试信息
                    Log.d(TAG, "isRegistered: " + event.isRegistered());
                    Log.d(TAG, "QR Code: " + event.getQrCode());

                    // 更新活动详情 UI
                    textViewTitle.setText("Event Title: " + event.getTitle());
                    textViewDescription.setText("Event Description: " + event.getDescription());
                    textViewStartTime.setText("Start Time: " + event.getFormattedStartTime());
                    textViewEndTime.setText("End Time: " + event.getFormattedEndTime());
                    textViewLocation.setText("Location: " + event.getLocation());

                    // 检查用户是否已注册
                    boolean isRegistered = event.isRegistered();

                    if (isRegistered) {
                        buttonRegister.setText("Registered");
                        buttonRegister.setEnabled(false);

                        // 显示 QR Code
                        String qrCode = event.getQrCode();
                        if (qrCode != null) {
                            showQRCode(qrCode);
                        } else {
                            Log.w(TAG, "QR Code is null. Hiding QR Code.");
                            hideQRCode();
                        }
                    } else {
                        buttonRegister.setText("Register");
                        buttonRegister.setEnabled(true);
                        hideQRCode();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load event details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to fetch event details", t);
            }
        });
    }

    private void registerForEvent(int eventId, String userToken) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // 构建请求体
        JSONObject requestBodyJson = new JSONObject();
        try {
            requestBodyJson.put("userToken", userToken); // 传递用户 Token
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
                        JSONObject responseBody = new JSONObject(response.body().string());
                        if (responseBody.has("qr_code")) {
                            String qrCode = responseBody.getString("qr_code");
                            Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                            showQRCode(qrCode); // 显示 QR Code
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
                Log.e(TAG, "Failed to register for event", t);
            }
        });
    }

    private void showQRCode(String qrCode) {
        if (qrCodeImageView == null) {
            throw new IllegalStateException("qrCodeImageView is not found. Check your layout.");
        }

        try {
            Log.d(TAG, "Decoding QR Code: " + qrCode);

            // 将 Base64 编码的 QR Code 转换为 Bitmap
            byte[] decodedString = Base64.decode(qrCode.split(",")[1], Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (decodedByte == null) {
                Log.e(TAG, "Failed to decode QR Code.");
                Toast.makeText(getContext(), "Failed to decode QR Code!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 在 ImageView 中显示 QR Code
            qrCodeImageView.setImageBitmap(decodedByte);
            qrCodeImageView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "Error displaying QR Code.", e);
            Toast.makeText(getContext(), "Failed to display QR Code!", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideQRCode() {
        if (qrCodeImageView != null) {
            qrCodeImageView.setVisibility(View.GONE); // 隐藏 QR Code
        }
    }
}