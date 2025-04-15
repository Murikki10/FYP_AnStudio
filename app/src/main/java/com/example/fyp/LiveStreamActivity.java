package com.example.fyp;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
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

public class LiveStreamActivity extends AppCompatActivity {

    private WebView webView;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private EditText messageInput;
    private Button sendButton;

    private WebSocket webSocket;
    private List<ChatMessage> chatMessages;

    private static final String SOCKET_URL = "ws://13.239.37.192:8080"; // 替換為您的 WebSocket 地址
    private static final String CURRENT_USER = "CurrentUser"; // 假設當前用戶名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream);

        // 初始化視圖
        webView = findViewById(R.id.webView);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // 配置 WebView
        setupWebView();

        // 配置 RecyclerView 和 WebSocket
        setupChatRoom();

        // 發送消息按鈕
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty() && webSocket != null) {
                sendMessage(message);
                messageInput.setText(""); // 清空輸入框
            }
        });
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
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SOCKET_URL).build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d("WebSocket", "Connected to server");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e("WebSocket", "Connection failed: " + t.getMessage());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("WebSocket", "Raw message: " + text);

                runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        String user = jsonObject.getString("user");
                        String content = jsonObject.getString("content");

                        // 更新消息列表
                        chatMessages.add(new ChatMessage(user, content));
                        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                    } catch (JSONException e) {
                        Log.e("WebSocket", "JSON parsing error: " + e.getMessage());
                    }
                });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Activity destroyed");
            Log.d("WebSocket", "WebSocket closed");
        }
    }
}