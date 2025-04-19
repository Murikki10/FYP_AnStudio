package com.example.fyp;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class TokenUtils {

    public static String getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = new String(Base64.decode(parts[1], Base64.DEFAULT));
                JSONObject jsonObject = new JSONObject(payload);
                return jsonObject.getString("userId"); // 假設 Token 的 payload 中有 userId 字段
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}