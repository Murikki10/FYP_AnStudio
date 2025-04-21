package com.example.fyp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class LiveStreamFragment extends Fragment {

    private WebView webView;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private EditText messageInput;
    private Button sendButton;
    private TextView connectionStatus;

    private WebSocket webSocket;
    private List<ChatMessage> chatMessages;

    private static final String SOCKET_URL = "ws://13.239.37.192:8080"; // 替換為您的 WebSocket 地址
    private static final String CURRENT_USER = "CurrentUser"; // 假設當前用戶名

    private Handler handler = new Handler(); // 用於延遲隱藏提示的 Handler

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_stream, container, false);

        // 初始化視圖
        webView = view.findViewById(R.id.webView);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);
        connectionStatus = view.findViewById(R.id.connectionStatus);

        // 配置 WebView
        setupWebView();

        // 配置聊天
        setupChatRoom();

        // 發送消息按鈕
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty() && webSocket != null) {
                sendMessage(message);
                messageInput.setText("");
            }
        });

        return view;
    }

    private void setupWebView() {
        // 配置 WebView 播放 YouTube 直播
        String youtubeEmbedUrl = "https://www.youtube.com/embed/qI8xWP2-jsQ"; // 替換為您的 YouTube 視頻 ID
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(youtubeEmbedUrl);
    }

    private void setupChatRoom() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, CURRENT_USER); // 傳入當前用戶名
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SOCKET_URL).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d("WebSocket", "Connected to server");

                // 更新連接狀態為「已連接」
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showConnectionStatus("Chat Room Has Been Connect", android.R.color.holo_green_dark);
                    });
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e("WebSocket", "Connection failed: " + t.getMessage());

                // 更新連接狀態為「連接失敗」
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showConnectionStatus("Connection failed", android.R.color.holo_red_dark);
                    });
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("WebSocket", "Raw message: " + text);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(text);
                            String user = jsonObject.getString("user");
                            String content = jsonObject.getString("content");

                            // 隱藏連接狀態提示
                            hideConnectionStatus();

                            // 更新消息列表
                            chatMessages.add(new ChatMessage(user, content));
                            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                            chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                        } catch (JSONException e) {
                            Log.e("WebSocket", "JSON parsing error: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void sendMessage(String message) {
        if (webSocket != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user", CURRENT_USER); // 當前用戶名
                jsonObject.put("content", message);
                webSocket.send(jsonObject.toString());
                Log.d("WebSocket", "Message sent: " + jsonObject.toString());
            } catch (JSONException e) {
                Log.e("WebSocket", "Error creating JSON object: " + e.getMessage());
            }
        }
    }

    // 顯示連接狀態提示
    private void showConnectionStatus(String message, int colorResId) {
        connectionStatus.setText(message);
        connectionStatus.setBackgroundColor(getResources().getColor(colorResId));
        connectionStatus.setVisibility(View.VISIBLE);

        // 延遲 3 秒後自動隱藏狀態
        handler.postDelayed(this::hideConnectionStatus, 3000);
    }

    // 隱藏連接狀態提示
    private void hideConnectionStatus() {
        connectionStatus.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webSocket != null) {
            webSocket.close(1000, "Fragment destroyed");
            Log.d("WebSocket", "WebSocket closed");
        }
        handler.removeCallbacksAndMessages(null); // 清除所有延遲任務
    }
}